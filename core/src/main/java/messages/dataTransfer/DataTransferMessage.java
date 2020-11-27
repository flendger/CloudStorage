package messages.dataTransfer;

import messages.AbstractMessage;
import messages.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * Message structure:
 *  msg length - int
 *  msg type id - short
 *  msg id - int
 *  isFirst - byte
 *  isEOF - byte
 *  fileId - int
 *  fileName length - int
 *  --- end of header ---
 *  fileName - String (byte[])
 *  data - byte[]
 */

public class DataTransferMessage extends AbstractMessage {

    public static final int HEADER_LENGTH = 20;
    public static final int MAX_DATA_BUFFER_SIZE = 8192;
    private final ByteBuffer bf = ByteBuffer.allocate(HEADER_LENGTH + MAX_DATA_BUFFER_SIZE + 1024); //1024 - for fileName

    private int fileId;
    private String fileName;
    private byte[] data;
    private boolean isFirst;
    private boolean isEOF;


    public DataTransferMessage() {
        super(MessageType.DATA_TRANSFER);
    }

    public DataTransferMessage(byte[] bytes) {
        super(MessageType.DATA_TRANSFER);
        fromBytes(bytes);
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data, int len) {
        this.data = Arrays.copyOf(data, len);
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isEOF() {
        return isEOF;
    }

    public void setEOF(boolean EOF) {
        isEOF = EOF;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public byte[] toBytes() {
        bf.clear();

        bf.putInt(getLength());
        bf.putShort(msgType.getId());
        bf.putInt(msgId);
        bf.put((byte) (isFirst ? 1 : 0));
        bf.put((byte) (isEOF ? 1 : 0));
        bf.putInt(fileId);

        byte[] nameBytes = fileName.getBytes();
        bf.putInt(nameBytes.length);
        bf.put(nameBytes);

        bf.put(data);

        bf.flip();
        return Arrays.copyOf(bf.array(), bf.limit());
    }

    @Override
    public void fromBytes(byte[] bytes) {
        bf.clear();
        bf.put(bytes);
        bf.flip();

        int length = bf.getInt();
        bf.getShort(); //message type
        msgId = bf.getInt();

        isFirst = bf.get() == (byte) 1;
        isEOF = bf.get() == (byte) 1;
        fileId = bf.getInt();

        int nameLength = bf.getInt();
        byte[] nameBytes = new byte[nameLength];
        int pos = 0;
        while (bf.hasRemaining() && pos < nameLength) {
            nameBytes[pos] = bf.get();
            pos++;
        }
        fileName = new String(nameBytes, StandardCharsets.UTF_8);

        data = new byte[(int) (length - HEADER_LENGTH - nameLength)];
        if (bf.hasRemaining()) bf.get(data);
    }

    @Override
    public int getLength() {
        return HEADER_LENGTH + fileName.getBytes().length + data.length;
    }

    @Override
    public String toString() {
        return "DataTransferMessage{" +
                "msgId=" + msgId +
                ", msgLength=" + getLength() +
                ", msgType=" + msgType +
                ", fileID=" + fileId +
                ", fileName=" + fileName +
                ", isFirst=" + isFirst +
                ", isEOF=" + isEOF +
                ", data length='" + data.length + '\'' +
                '}';
    }
}
