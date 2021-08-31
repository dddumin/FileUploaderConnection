package org.dumin;

import exceptions.NameMatchException;
import exceptions.NodeNotFoundException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.BTree;
import model.FileInfo;
import org.apache.commons.io.FileUtils;
import repository.FilesRepository;
import repository.FolderRepository;
import util.Constants;
import util.FilesUtil;
import util.StringPathUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class MainController {
    public ListView<FileInfo> listViewServerFiles;
    public ListView<FileInfo> listViewUserFiles;
    public Button btnOpenServiceDir;
    public Button btnOpenUserDir;
    public Button btnBackServer;
    public Button btnBackUser;
    public ComboBox<String> comboBoxNameDisk;
    public Label labelCurUser;
    public Label labelCurServer;
    public Button btnDownloadFromServer;
    public Button btnInfoFileFromServer;
    public Button btnUploadFileFromUser;
    public Button btnInfoFileFromUser;
    public Button btnDeleteUserFile;
    public Button btnDeleteServerFile;
    private ArrayList<FileInfo> serverFilesList;
    private ArrayList<FileInfo> userFilesList;
    private String curUserDirPath;
    private String curServerDirPath;

    @FXML
    void initialize() {
        //Set User List Root (disks) in comboBox
        List<String> listRoots = Arrays.stream(File.listRoots())
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
        int index = listRoots.indexOf(Constants.USER_ROOT_PATH.split(Pattern.quote("\\"))[0] + "\\");
        this.comboBoxNameDisk.setItems(FXCollections.observableList(listRoots));
        this.comboBoxNameDisk.getSelectionModel().select(index);

        //Disabled buttons Download, Info, Delete
        this.setDisableButtonsServer(true);
        this.setDisableButtonsUser(true);

        //Create List Files for Current User Path and set value to ListView User
        this.curUserDirPath = Constants.USER_ROOT_PATH;
        this.updateUserFilesList(this.curUserDirPath);

        //Download List Files for Current Server Path and set value to ListView Server
        this.curServerDirPath = "";
        this.updateServerFilesList(this.curServerDirPath);

        //Handler for select user's file  and enabled/disabled button "Open"
        this.listViewUserFiles.setOnMouseClicked(mouseEvent -> {
            FileInfo selectedItem = this.listViewUserFiles.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.isFolder()) {
                this.btnOpenUserDir.setDisable(false);
            } else if (selectedItem != null && !selectedItem.isFolder()) {
                this.btnOpenUserDir.setDisable(true);
            }
            if (selectedItem != null) {
                this.setDisableButtonsUser(false);
            }
        });

        //Handler for select server's file and enabled/disabled button "Open"
        this.listViewServerFiles.setOnMouseClicked(mouseEvent -> {
            FileInfo selectedItem = this.listViewServerFiles.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.isFolder()) {
                this.btnOpenServiceDir.setDisable(false);
            } else if (selectedItem != null && !selectedItem.isFolder()) {
                this.btnOpenServiceDir.setDisable(true);
            }
            if (selectedItem != null) {
                this.setDisableButtonsServer(false);
            }
        });
    }

    /**
     * Method for enabled/disabled for User's Buttons (Upload, Info, Delete)
     */
    private void setDisableButtonsUser(boolean isDisabled) {
        this.btnDeleteUserFile.setDisable(isDisabled);
        this.btnInfoFileFromUser.setDisable(isDisabled);
        this.btnUploadFileFromUser.setDisable(isDisabled);
    }

    /**
     * Method for enabled/disabled Server's Buttons (Upload, Info, Delete)
     */
    private void setDisableButtonsServer(boolean isDisabled) {
        this.btnDeleteServerFile.setDisable(isDisabled);
        this.btnInfoFileFromServer.setDisable(isDisabled);
        this.btnDownloadFromServer.setDisable(isDisabled);
    }

    /**
     * Method for update User's File List By Current User Path
     */
    private void updateUserFilesList(String path) {
        this.userFilesList = FilesUtil.getListFileInfoByPath(path);
        this.listViewUserFiles.setItems(FXCollections.observableList(this.userFilesList));
        this.curUserDirPath = path;
        this.labelCurUser.setText(StringPathUtil.getValueLabelForUserByPath(this.curUserDirPath));
    }

    /**
     * Method for update Server's File List By Current Server Path
     */
    private void updateServerFilesList(String path) {
        try {
            this.serverFilesList = new FilesRepository(path, "GET").getFileInfoList();
            this.listViewServerFiles.setItems(FXCollections.observableList(this.serverFilesList));
            this.curServerDirPath = path;
            this.labelCurServer.setText(StringPathUtil.getValueLabelForServerByPath(path));
        } catch (IOException e) {
            App.showAlert("Ошибка", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handler for button "Open" for user's side
     */
    public void openDirectoryUser(ActionEvent actionEvent) {
        FileInfo selectedItem = this.listViewUserFiles.getSelectionModel().getSelectedItem();
        this.updateUserFilesList(selectedItem.getAbsolutePath());
        this.btnOpenUserDir.setDisable(true);
        this.setDisableButtonsUser(true);
    }

    /**
     * Handler for button "Open" for server's side
     */
    public void openDirectoryServer(ActionEvent actionEvent) {
        FileInfo selectedItem = this.listViewServerFiles.getSelectionModel().getSelectedItem();
        this.updateServerFilesList(selectedItem.getAbsolutePath());
        this.btnOpenServiceDir.setDisable(true);
        this.setDisableButtonsServer(true);
    }

    /**
     * Handler for button "↑" for user's side
     */
    public void backUser(ActionEvent actionEvent) {
        String[] split = this.curUserDirPath.split(Pattern.quote("\\"));
        if (split.length == 1) {
            App.showAlert("Ошибка", "Вы находитесь в корневом каталоге", Alert.AlertType.ERROR);
            return;
        }
        this.updateUserFilesList(StringPathUtil.getParentPathForUser(this.curUserDirPath));
        this.btnOpenServiceDir.setDisable(true);
        this.setDisableButtonsServer(true);
    }

    /**
     * Handler for button "↑" for server's side
     */
    public void backServer(ActionEvent actionEvent) {
        if (this.curServerDirPath.equals("")) {
            App.showAlert("Ошибка", "Вы находитесь в корневом каталоге", Alert.AlertType.ERROR);
        } else {
            this.updateServerFilesList(StringPathUtil.getParentPathForServer(this.curServerDirPath));
            this.btnOpenServiceDir.setDisable(true);
            this.setDisableButtonsServer(true);
        }
    }

    /**
     * Handler for select comboBox disks for user's side
     */
    public void goToDisk(ActionEvent actionEvent) {
        String selectedItem = this.comboBoxNameDisk.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            this.updateUserFilesList(selectedItem);
            this.btnOpenServiceDir.setDisable(true);
            this.setDisableButtonsServer(true);
        }
    }

    /**
     * Method for uploading files to the server
     * @param actionEvent
     */
    public void uploadFile(ActionEvent actionEvent) {
        FileInfo selectedItem = this.listViewUserFiles.getSelectionModel().getSelectedItem();
        uploadFile(selectedItem, this.curServerDirPath);

        this.updateServerFilesList(this.curServerDirPath);
    }


    /**
     * Recursive method for uploading files to the server
     * @param selectedItem - current FileInfo
     * @param pathForServer - path for saving on server
     */
    private void uploadFile(FileInfo selectedItem, String pathForServer) {
        if (!selectedItem.isFolder()) {
            try {
                if (FilesUtil.isFileExist(selectedItem, new FilesRepository(pathForServer, "GET").getFileInfoList())) {
                    App.showAlert("Информация", "Файл уже существует", Alert.AlertType.INFORMATION);
                } else {
                    FilesRepository.uploadFile(new File(selectedItem.getAbsolutePath()), pathForServer, "cp866");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BTree<FileInfo> bTreeServer = new FolderRepository(pathForServer + File.separator + selectedItem.getName(), "GET").getBTree();
                if (bTreeServer == null) {
                    File zipFile = FilesUtil.getZipFile(selectedItem.getAbsolutePath());
                    FilesRepository.uploadFile(zipFile, pathForServer, "utf-8");
                    zipFile.delete();
                } else {
                    try {
                        BTree<FileInfo> bTreeUser = FilesUtil.getBTreeFileInfo(selectedItem.getAbsolutePath());
                        ArrayList<FileInfo> differenceBetweenBTree = BTree.getDifferenceBetweenBTree(bTreeUser, bTreeServer);

                        differenceBetweenBTree.forEach(fileInfo -> {
                            String pathServer = this.curServerDirPath.equals("") ? StringPathUtil.getPathForServer(fileInfo.getParentPath(), this.curUserDirPath)
                                    : this.curServerDirPath + File.separator + StringPathUtil.getPathForServer(fileInfo.getParentPath(), this.curUserDirPath);
                            uploadFile(fileInfo, pathServer);
                        });
                    } catch (NodeNotFoundException | NameMatchException e) {
                        e.printStackTrace();
                        App.showAlert("Ошибка", "Ошибка при создании иерархии папки!", Alert.AlertType.ERROR);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                App.showAlert("Информация", "Ошибка при загрузке файла!", Alert.AlertType.INFORMATION);
            }
        }
    }

    /**
     * Method for downloading files from the server
     * @param actionEvent
     */
    public void downloadFile(ActionEvent actionEvent) {
        FileInfo selectedItem = this.listViewServerFiles.getSelectionModel().getSelectedItem();
        this.downloadFile(selectedItem, this.curUserDirPath);

        this.updateUserFilesList(this.curUserDirPath);
    }

    /**
     * Recursive Method for downloading files from the server
     * @param fileInfo - current FileInfo
     * @param pathOut - parent path for file from server
     */
    public void downloadFile(FileInfo fileInfo, String pathOut) {
        try {
            if (!fileInfo.isFolder() && FilesUtil.isFileExist(fileInfo, FilesUtil.getListFileInfoByPath(pathOut))) {
                App.showAlert("Информация", "Файл уже существует", Alert.AlertType.INFORMATION);
            } else {
                String fileOutPath = pathOut + File.separator
                        + FilesUtil.getNameFile(pathOut, fileInfo.getName());
                if (fileInfo.isFolder()) {
                    fileOutPath += ".zip";
                }
                FilesRepository.downloadFile(fileInfo.getParentPath(), fileInfo.getName(), fileOutPath);
                if (fileOutPath.endsWith(".zip")) {
                    FilesUtil.processArchive(fileOutPath);
                }
            }
        } catch (IOException | NodeNotFoundException | NameMatchException e) {
            e.printStackTrace();
        }
    }

    public void infoFileServer(ActionEvent actionEvent) {
    }

    public void infoFileUser(ActionEvent actionEvent) {
    }

    /**
     * Handler for button "Delete" for user's side
     */
    public void deleteFileUser(ActionEvent actionEvent) {
        FileInfo selectedItem = this.listViewUserFiles.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            File file = new File(selectedItem.getAbsolutePath());
            if (!file.isDirectory()) {
                if (file.delete()) {
                    App.showAlert("Информация", "Файл успешно удалён!!!", Alert.AlertType.INFORMATION);
                } else {
                    App.showAlert("Ошибка", "Ошибка при удалении файла!!!", Alert.AlertType.ERROR);
                }
            } else {
                try {
                    FileUtils.deleteDirectory(file);
                    App.showAlert("Информация", "Файл успешно удалён!!!", Alert.AlertType.INFORMATION);
                } catch (IOException e) {
                    App.showAlert("Ошибка", "Ошибка при удалении файла!!!", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
            this.updateUserFilesList(this.curUserDirPath);
            this.btnOpenUserDir.setDisable(true);
            this.setDisableButtonsUser(true);
        } else {
            App.showAlert("Ошибка", "Выберите файл для удаления!!!", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handler for button "Delete" for server's side
     */
    public void deleteFileServer(ActionEvent actionEvent) {
        FileInfo selectedItem = this.listViewServerFiles.getSelectionModel().getSelectedItem();
        try {
            this.serverFilesList = new FilesRepository(selectedItem.getAbsolutePath(), "DELETE").getFileInfoList();
            this.listViewServerFiles.setItems(FXCollections.observableList(this.serverFilesList));
            App.showAlert("Информация", "Файл успешно удалён!!!", Alert.AlertType.INFORMATION);
            this.btnOpenServiceDir.setDisable(true);
            this.setDisableButtonsServer(true);
        } catch (IOException e) {
            App.showAlert("Ошибка", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
