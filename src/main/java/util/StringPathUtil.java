package util;


import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class StringPathUtil {
    public static String getValueLabelForUserByPath(String curUserDirPath) {
        String[] split = curUserDirPath.split(Pattern.quote("\\"));
        int length = split.length;
        if (length > 2) {
            return split[0] + File.separator + "..." + File.separator + split[length - 1];
        }
        return curUserDirPath;
    }

    public static String getValueLabelForServerByPath(String curServerDirPath) {
        if (curServerDirPath.equals("")) {
            return "Server Root";
        }
        String[] split = curServerDirPath.split(Pattern.quote("\\"));
        int length = split.length;
        if (length == 1) {
            return "Server Root\\" + split[0];
        } else {
            return "Server Root\\...\\" + split[length - 1];
        }
    }

    public static String getParentPathForUser(String curUserDirPath) {
        return new File(curUserDirPath).getParent();
    }

    public static String getParentPathForServer(String curServerDirPath) {
        String[] split = curServerDirPath.split(Pattern.quote("\\"));
        return String.join(File.separator, Arrays.copyOf(split, split.length - 1));
    }

    public static String getFileNameWithoutExtension(String fileName) {
        String[] split = fileName.split("\\.");
        return String.join(".", Arrays.copyOf(split, split.length - 1));
    }

    public static String getPathForServer(String path, String curUserPath) {
        if (Arrays.stream(File.listRoots()).filter(file -> file.getAbsolutePath().equals(curUserPath)).findAny().orElse(null) != null) {
            return path.replace(curUserPath, "");
        }
        return path.replace(curUserPath + File.separator, "");
    }

    public static String getFileExtension(String fileName) {
        String[] nameSplit = fileName.split("\\.");
        return "." + nameSplit[nameSplit.length - 1];
    }
}
