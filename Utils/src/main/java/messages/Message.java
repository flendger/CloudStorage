package messages;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;

public class Message {
    private final ByteBuffer bf = ByteBuffer.allocate(8192);

    private MessageType msgType;
    private int msgId;
    private String parameter;

    public Message() {
    }

    public Message(MessageType msgType) {
        this.msgType = msgType;
        setDefaultMsgId();
    }

    public void setDefaultMsgId() {
        this.msgId = LocalTime.now().toSecondOfDay();
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public byte[] toBytes() {
        bf.clear();
        bf.putInt(msgId);
        bf.putShort(msgType.getId());
        if (isNotFile()) {
            bf.put((byte) 1);
            bf.putLong(getDataLength());
            bf.put(parameter.getBytes());
        } else {
            bf.put((byte) 0);
            bf.putLong(0);
        }
        bf.flip();
        return Arrays.copyOf(bf.array(), bf.limit());
    }

    public void fromBytes(byte[] bytes) {
        bf.clear();
        bf.put(bytes);
        bf.flip();

        msgId = bf.getInt();
        msgType = MessageType.findById(bf.getShort());
        if (bf.get() == (byte) 1) {
            long length = bf.getLong();
            byte[] parBytes = new byte[(int) length];
            int pos = 0;
            while (bf.hasRemaining() ) {
                parBytes[pos] = bf.get();
                pos++;
            }
            parameter = new String(parBytes, StandardCharsets.UTF_8);
        }
    }

    public boolean isNotFile() {
        return msgType.isNotFile();
    }

    public long getDataLength() {
        return parameter.getBytes().length;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public int getMsgId() {
        return msgId;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s]", msgType.toString(), parameter);
    }
}
