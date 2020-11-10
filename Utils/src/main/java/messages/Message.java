package messages;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * Message protocol:
 *      msg_length - [long] header (15 bytes) + data (non-fix)
 *      msg_data_type - [byte] 0 - file, 1 - not file (for example, command (String))
 *      msg_id - [integer] message id
 *      msg_type_id = [short] message command (id)
 *      data = [ byte[] ] (if exists) transferring data (file, command, etc)
 */

public class Message {

    public static final int HEADER_LENGTH = 15;

    private final ByteBuffer bf = ByteBuffer.allocate(2048);

    private MessageType msgType;
    private int msgId;
    private String parameter;

    public static Message of(byte[] bytes) {
        Message msg = new Message();
        msg.fromBytes(bytes);
        return msg;
    }

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
        if (isNotFile()) {
            bf.putLong(HEADER_LENGTH + getDataLength());
            bf.put((byte) 1);
        } else {
            bf.putLong(HEADER_LENGTH);
            bf.put((byte) 0);
        }
        bf.putInt(msgId);
        bf.putShort(msgType.getId());
        if (isNotFile()) {
            bf.put(parameter.getBytes());
        }
        bf.flip();
        return Arrays.copyOf(bf.array(), bf.limit());
    }

    public void fromBytes(byte[] bytes) {
        bf.clear();
        bf.put(bytes);
        bf.flip();

        long length = bf.getLong();
        boolean notFile = bf.get() == (byte) 1;
        msgId = bf.getInt();
        msgType = MessageType.findById(bf.getShort());
        if (notFile) {
            byte[] parBytes = new byte[(int) (length-HEADER_LENGTH)];
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
