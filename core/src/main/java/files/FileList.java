package files;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileList implements Serializable {
    private List<FileInfo> fileList = new ArrayList<>();

    public void add(FileInfo fileInfo) {
        fileList.add(fileInfo);
    }

    public List<FileInfo> getFileList() {
        return fileList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        fileList.stream()
                .forEach(arg -> sb.append(arg.toString()).append("\n"));
        String res = sb.toString();
        sb.setLength(0);
        return res;
    }
}
