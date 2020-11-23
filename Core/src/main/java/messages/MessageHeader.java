package messages;

import java.nio.ByteBuffer;

public class MessageHeader {

    private final ByteBuffer bf = ByteBuffer.allocate(10);

    public int msgId;
    public int msgLength;
    public MessageType msgType;

    public MessageHeader(byte[] bytes) {
        fromBytes(bytes);
    }

    public void fromBytes(byte[] bytes) {
        if (bytes.length < 10) {
            return;
        }

        bf.clear();
        bf.put(bytes, 0, bf.capacity());
        bf.flip();

        msgLength = bf.getInt();
        msgType = MessageType.getInstance(bf.getShort());
        msgId = bf.getInt();
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "msgId=" + msgId +
                ", msgLength=" + msgLength +
                ", msgType=" + msgType +
                '}';
    }
}
