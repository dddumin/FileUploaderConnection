package util;

import exceptions.NameMatchException;
import exceptions.NodeNotFoundException;
import model.BTree;
import model.FileInfo;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import program.Program;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilesUtil {
    /**
     * method for Collect FileInto by path
     * @param path
     * @return
     */
    public static ArrayList<FileInfo> getListFileInfoByPath(String path) {
        File rootFile = new File(path);
        File[] files = rootFile.listFiles();
        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                boolean isDirectory = file.isDirectory();
                String hash = null;
                if (!isDirectory) {
                    try(InputStream inputStream = Files.newInputStream(file.toPath())) {
                        hash = DigestUtils.md5Hex(inputStream);
                    } catch (IOException ignored) {
                    }
                }
                fileInfoArrayList.add(new FileInfo(file.getName(), file.getParent(), isDirectory, hash));
            }
        }
        return fileInfoArrayList;
    }

    /**
     * Method of checking a file among a list of files
     * @param fileInfo
     * @param fileInfoList
     * @return
     */
    public static boolean isFileExist(FileInfo fileInfo, List<FileInfo> fileInfoList) {
        String fileNameWithoutExtension = StringPathUtil.getFileNameWithoutExtension(fileInfo.getName());

        if (fileInfoList.stream()
                .filter(
                        curFileInfo -> curFileInfo.getName().equals(fileInfo.getName())
                                && curFileInfo.getHash().equals(fileInfo.getHash())
                ).findAny().orElse(null) != null) {
            return true;
        }
        String fileNameWithVersion = fileNameWithoutExtension + "(Version";
        return fileInfoList.stream()
                .filter(curFileInfo -> curFileInfo.getName().contains(fileNameWithVersion)
                        && curFileInfo.getHash().equals(fileInfo.getHash())).findAny().orElse(null) != null;
    }

    /**
     * Method for create Zip file by path
     * @param path
     * @return
     */
    public static File getZipFile(String path) {
        ZipFile zipFile = new ZipFile(path + ".zip");
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                try {
                    if (file.isDirectory()) {
                        zipFile.addFolder(file);
                    } else {
                        zipFile.addFile(file);
                    }
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            });
        }
        return zipFile.getFile();
    }

    /**
     * Method create BTree<FileInfo> by path
     * @param path
     * @return
     * @throws NodeNotFoundException
     * @throws NameMatchException
     */
    public static BTree<FileInfo> getBTreeFileInfo(String path) throws NodeNotFoundException, NameMatchException {
        File root = new File(path);
        BTree<FileInfo> bTree = new BTree<>();
        if (root.exists() && root.isDirectory()) {
            bTree.add(null, "", new FileInfo(root.getName(), root.getAbsolutePath(), true, null));
            FilesUtil.fillBTree(bTree, root, "");
        }
        return bTree;
    }

    /**
     * Recursive Method for fill BTree<FileInfo>
     * @param bTree
     * @param rootFile
     * @param rootNodeName
     * @throws NodeNotFoundException
     * @throws NameMatchException
     */
    private static void fillBTree(BTree<FileInfo> bTree, File rootFile, String rootNodeName) throws NodeNotFoundException, NameMatchException {
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean isDirectory = file.isDirectory();
                String hash = null;
                if (!isDirectory) {
                    try(InputStream inputStream = Files.newInputStream(file.toPath())) {
                        hash = DigestUtils.md5Hex(inputStream);
                    } catch (IOException ignored) {
                    }
                }
                String nodeName = rootNodeName.equals("") ? file.getName() : rootNodeName + File.separator + file.getName();
                bTree.add(rootNodeName, nodeName, new FileInfo(file.getName(), file.getParent(), isDirectory, hash));
                if (isDirectory) {
                    fillBTree(bTree, file, nodeName);
                }
            }
        }
    }

    /**
     * Method for create file name with version
     * @param path
     * @param fileName
     * @return
     */
    public static String getNameFile(String path, String fileName) {
        String fileExtension = StringPathUtil.getFileExtension(fileName);
        String fileNameWithoutExtension = StringPathUtil.getFileNameWithoutExtension(fileName);

        File rootFolder = new File(path);
        String[] list = rootFolder.list();
        if (list != null) {
            List<String> names = Arrays.asList(list);
            if (!names.contains(fileNameWithoutExtension + fileExtension)) {
                return fileName;
            } else {
                String fileNameWithVersion = fileNameWithoutExtension + "(Version";
                int count = 0;
                for (String name : names) {
                    if (name.contains(fileNameWithVersion)) {
                        count++;
                    }
                }
                return fileNameWithoutExtension + String.format("(Version %d)", count + 1) + fileExtension;
            }
        }
        return fileName;
    }

    /**
     * Method for collect FileInfo with Children by Path
     * @param path
     * @return
     */
    public static ArrayList<FileInfo> getListFileInfoByPathWithChildren (String path) {
        File file = new File(path);
        ArrayList<FileInfo> list = new ArrayList<>();
        fillListFileInfoWithChildren(file, list);
        return list;
    }

    /**
     * Recursive method fill ArrayList<FileInfo>
     * @param rootFile
     * @param fileInfoArrayList
     */
    private static void fillListFileInfoWithChildren (File rootFile, ArrayList<FileInfo> fileInfoArrayList) {
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean isDirectory = file.isDirectory();
                if (isDirectory) {
                    fileInfoArrayList.add(new FileInfo(file.getName(), file.getParent(), true, null));
                    fillListFileInfoWithChildren(file, fileInfoArrayList);
                } else {
                    try(InputStream inputStream = Files.newInputStream(file.toPath())) {
                        fileInfoArrayList.add(new FileInfo(file.getName(), file.getParent(), false, DigestUtils.md5Hex(inputStream)));
                    } catch (IOException ignored) {
                        System.out.println("Не удалось обработать файл " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Method for process archive by path
     * @param path
     * @throws NodeNotFoundException
     * @throws NameMatchException
     * @throws IOException
     */
    public static void processArchive(String path) throws NodeNotFoundException, NameMatchException, IOException {
        ZipFile zipFile = new ZipFile(path);
        File file = zipFile.getFile();
        String parentPathZipFile = file.getParent();

        BTree<FileInfo> bTreeUser = FilesUtil.getBTreeFileInfo(parentPathZipFile);
        String processFolderName = StringPathUtil.getFileNameWithoutExtension(file.getName())  + "-Processing";

        String processFolder = StringPathUtil.getFileNameWithoutExtension(file.getAbsolutePath())  + "-Processing";
        String folderForExtract = processFolder + File.separator + StringPathUtil.getFileNameWithoutExtension(file.getName());

        zipFile.extractAll(folderForExtract);

        ArrayList<FileInfo> listFileInfoByPathWithChildren = getListFileInfoByPathWithChildren(folderForExtract);
        listFileInfoByPathWithChildren.stream().filter(fileInfo -> fileInfo.getName().endsWith(".zip")).forEach(fileInfo -> {
            try {
                processArchive(fileInfo.getAbsolutePath());
            } catch (IOException | NodeNotFoundException | NameMatchException e) {
                System.out.println("Ошибка обработки внутреннего архива");
                e.printStackTrace();
            }
        });
        BTree<FileInfo> bTreeFileInfoProcessingFolder = FilesUtil.getBTreeFileInfo(processFolder);

        ArrayList<FileInfo> differenceBetweenBTree = BTree.getDifferenceBetweenBTree(bTreeFileInfoProcessingFolder, bTreeUser);

        System.out.println(Program.getJson(differenceBetweenBTree));

        for (FileInfo fileInfo : differenceBetweenBTree) {
            if (fileInfo.isFolder() || !isFileExist(fileInfo, getListFileInfoByPath(fileInfo.getParentPath().replace(processFolderName, "")))) {
                Path source = Paths.get(fileInfo.getAbsolutePath());
                System.out.println(fileInfo.getParentPath());
                System.out.println(fileInfo.getAbsolutePath());

                String targetParentPath = fileInfo.getParentPath().replace(processFolderName, "");
                Path target = Paths.get(targetParentPath + File.separator + getNameFile(targetParentPath, fileInfo.getName()));

                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

            }
        }
        file.delete();
        FileUtils.deleteDirectory(new File(processFolder));
    }
}
