package model;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileInfo {
    private String name;
    private String parentPath;
    private boolean isFolder;
    private String hash;

    public FileInfo(String name, String parentPath, boolean isFolder, String hash) {
        this.name = name;
        this.parentPath = parentPath;
        this.isFolder = isFolder;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getAbsolutePath() {
        if (Arrays.stream(File.listRoots()).filter(file -> file.getAbsolutePath().equals(this.parentPath)).findAny().orElse(null) != null){
            return this.parentPath + this.name;
        }
        return this.parentPath.equals("") ? this.name : this.parentPath + File.separator + this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return isFolder == fileInfo.isFolder &&
                Objects.equals(name, fileInfo.name) &&
                Objects.equals(hash, fileInfo.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isFolder, hash);
    }

    @Override
    public String toString() {
        return this.getName();
    }

   /* @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", isFolder=" + isFolder +
                ", hash='" + hash + '\'' +
                '}';
    }*/
}
