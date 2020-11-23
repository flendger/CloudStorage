package files;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo implements Serializable {
    private final String name;
    private final Long size;
    private final boolean isDirectory;
    private final String path;
    private final LocalDateTime lastModified;

    public static FileInfo of(String pathString) {
        Path path = Paths.get(pathString);
        if (! Files.exists(path)) return null;

        return new FileInfo(path);
    }

    public static FileInfo of(Path path) {
        if (! Files.exists(path)) return null;

        return new FileInfo(path);
    }

    private FileInfo(Path path) {
        this.name = path.getFileName().toString();
        this.isDirectory = Files.isDirectory(path);
        long fSize = -1L;
        LocalDateTime fModified = LocalDateTime.MIN;
        try {
            if (!isDirectory) fSize = Files.size(path);
            fModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.size = fSize;
        this.lastModified = fModified;
        this.path = path.toString();
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", isDirectory=" + isDirectory +
                ", path='" + path + '\'' +
                '}';
    }
}
