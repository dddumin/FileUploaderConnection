package program;

import com.google.gson.GsonBuilder;
import util.GsonExclusionStrategy;
import util.StringPathUtil;


public class Program {
    public static void main(String[] args) {
        /*String valueLabelForServerByPath = StringPathUtil.getValueLabelForServerByPath("\\новая папка\\про");
        System.out.println(valueLabelForServerByPath);*/
        /*ArrayList<FileInfo> listFileInfoByPathForListView = FilesUtil.getListFileInfoByPathForListView("C:\\File_uploader_files\\Новая папка\\Новая папка");
        System.out.println(listFileInfoByPathForListView);*/

        //System.out.println(File.listRoots()[0].getAbsolutePath());

        //[FileInfo{name='2.txt', parentPath='C:\File_uploader_user\Новая папка', isFolder=false, hash='d41d8cd98f00b204e9800998ecf8427e'}, FileInfo{name='3.txt', parentPath='C:\File_uploader_user\Новая папка', isFolder=false, hash='d41d8cd98f00b204e9800998ecf8427e'}, FileInfo{name='Новый текстовый документ.txt', parentPath='C:\File_uploader_user\Новая папка\Новая папка', isFolder=false, hash='d41d8cd98f00b204e9800998ecf8427e'}, FileInfo{name='Новая папка (2)', parentPath='C:\File_uploader_user\Новая папка', isFolder=true, hash='null'}]
        String pathForServer = StringPathUtil.getPathForServer("C:\\Program Files\\Java\\jdk-15.0.2+7", "C:\\");
        System.out.println(pathForServer);
    }

    public static String getJson(Object object){
        return new GsonBuilder().addSerializationExclusionStrategy(new GsonExclusionStrategy()).setPrettyPrinting().create().toJson(object);
    }
}
