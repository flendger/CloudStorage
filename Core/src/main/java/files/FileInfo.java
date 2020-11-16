package files;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo implements Serializable {
    private String name;
    private Long length;
    private boolean isDirectory;
    private String path;

    public static FileInfo of(String pathString) {
        Path path = Path.of(pathString);
        if (! Files.exists(path)) return null;

        FileInfo fileInfo = new FileInfo();
        fileInfo.name = path.getFileName().toString();
        fileInfo.isDirectory = Files.isDirectory(path);
        try {
            fileInfo.length = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileInfo.path = path.toString();

        return fileInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", length=" + length +
                ", isDirectory=" + isDirectory +
                ", path='" + path + '\'' +
                '}';
    }
}
