package files;

public class FileTransferRecord {

    public final int fileId;
    public final String fileName;
    public final String tmpFileName;
    public final String dir;


    public FileTransferRecord(int fileId, String fileName, String tmpFileName, String dir) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.tmpFileName = tmpFileName;
        this.dir = dir;
    }
}
