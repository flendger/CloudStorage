package messages;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageHeader {
    private final ByteBuffer bf = ByteBuffer.allocate(8192);

    public short typeId;
    public int id;
    public boolean notFile;
    public long length;
    public MessageType type;

    public MessageHeader() {
    }

    public MessageHeader(byte[] bytes) {
        bf.clear();
        bf.put(bytes);
        bf.flip();

        id = bf.getInt();
        typeId = bf.getShort();
        type = MessageType.findById(typeId);
        notFile = bf.get() == (byte) 1;
        length = bf.getLong();
    }
}
