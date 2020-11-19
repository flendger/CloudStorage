package files;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public final class FileUtils {

    public static FileList getFilesList(String path) {
        FileList fileList = new FileList();

        try {
            Path parentPath = Paths.get(path);
            Files.walk(parentPath, 1)
                    .filter(arg -> !arg.equals(parentPath))
                    .forEach(arg -> fileList.add(FileInfo.of(arg.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileList;
    }

    public static void createDirIfNotExist(String dir) throws IOException {
        Path path = Path.of(dir);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            throw new NotDirectoryException("Path is not directory: " + dir);
        }

        if (! Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    public static String changeDir(String newDir, String curPath, String root) throws NoSuchFileException, NotDirectoryException {
        Path path = Path.of(curPath);

        if (newDir.equals("/")) {
            while (!path.equals(Path.of(root)) && path.getParent() != null) {
                path = path.getParent();
            }
            return path.toString();
        } else if(newDir.equals("..")) {
            if (path.equals(Path.of(root)) || path.getParent() == null) {
                return path.toString();
            }
            path = path.getParent();
        } else if (newDir.equals(".")) {
            return path.toString();
        } else {
            path = Path.of(curPath, newDir);
        }
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                return path.toString();
            } else {
                throw new NotDirectoryException(newDir);
            }
        } else {
            throw new NoSuchFileException(newDir);
        }
    }

    public static String makeDir(String dirName, String curPath) throws IOException {
        Path path = Path.of(curPath, dirName);
        if (Files.exists(path)){
            throw new FileAlreadyExistsException(dirName);
        }

        Files.createDirectory(path);
        return path.toString();
    }

    public static boolean fileExist(String fileName, String curPath) {
        return Files.exists(Path.of(curPath, fileName));
    }

    public static String touchFile(String fileName, String curPath) throws IOException{
        Path path = Path.of(curPath, fileName);
        if (Files.exists(path)){
            throw new FileAlreadyExistsException(fileName);
        }

        Files.createFile(path);
        return path.toString();
    }

    public static String removeFile(String fileName, String curPath) throws IOException{
        Path path = Path.of(curPath, fileName);
        if (!Files.exists(path)){
            throw new NoSuchFileException(fileName);
        }

        Files.delete(path);
        return path.toString();
    }

    public static String copyFile(String src, String dst) throws IOException {
        Path srcPath = Path.of(src);
        if (!Files.exists(srcPath) || Files.isDirectory(srcPath)) {
            throw new NoSuchFileException(src);
        }

        Path destPath = Path.of(dst);
        Path destDirPath = Files.isDirectory(destPath) ? destPath : destPath.getParent();

        if (!Files.exists(destDirPath)) {
            throw new NoSuchFileException(destDirPath.toString());
        }

        if (!Files.isDirectory(destPath)) {
            if (Files.exists(destPath)) {
                throw new FileAlreadyExistsException(dst);
            }
        } else {
            destPath = destDirPath.resolve(srcPath);
        }

        Files.copy(srcPath, destPath);
        return destPath.toString();
    }

    public static Stream<String> catFile(String fileName) throws IOException{
        Path path = Path.of(fileName);
        if (!Files.exists(path) || Files.isDirectory(path)){
            throw new NoSuchFileException(fileName);
        }

        return Files.newBufferedReader(path).lines();
    }

    public static int generateFileId() {
        return (int) (Math.random() * 100000);
    }

    public static String createTmpFile(int fileId, String curPath) throws IOException {
        String tmpName = "tmp" + fileId + generateFileId();
        Path path = Path.of(curPath, tmpName);
        Files.createFile(path);

        return path.toAbsolutePath().toString();
    }

    public static void writeDataToFile(String file, byte[] data) throws IOException {
        Files.write(Path.of(file),
                data,
                StandardOpenOption.APPEND);
    }

    public static void renameTmpToFile(String tmp, String dir, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder(fileName);
        int cnt = 0;
        Path path;
        do {
            path = Path.of(dir, sb.toString());
            cnt ++;
            sb.setLength(0);
            sb.append(removeExtension(fileName))
                    .append("_")
                    .append(cnt)
                    .append(".")
                    .append(getExtension(fileName));
        } while (Files.exists(path));
        sb.setLength(0);

        Files.move(Path.of(tmp), path);
    }

    /**
     * Remove the file extension from a filename, that may include a path.
     *
     * e.g. /path/to/myfile.jpg -> /path/to/myfile
     */
    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * Return the file extension from a filename, including the "."
     *
     * e.g. /path/to/myfile.jpg -> .jpg
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return filename.substring(index);
        }
    }

    private static final char EXTENSION_SEPARATOR = '.';

    public static int indexOfExtension(String filename) {

        if (filename == null) {
            return -1;
        }

        // Check that no directory separator appears after the
        // EXTENSION_SEPARATOR

        return filename.lastIndexOf(EXTENSION_SEPARATOR);
    }
}
