package messages.dataTransfer;

import messages.AbstractMessage;
import messages.MessageType;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataTransferMessage extends AbstractMessage {

    public static final int HEADER_LENGTH = 11;
    private final ByteBuffer bf = ByteBuffer.allocate(2048);

    private byte[] data;
    private boolean isEOF;

    public DataTransferMessage() {
        super(MessageType.DATA_TRANSFER);
    }

    public DataTransferMessage(byte[] bytes) {
        super(MessageType.DATA_TRANSFER);
        fromBytes(bytes);
    }

    @Override
    public byte[] toBytes() {
        bf.clear();

        bf.putInt(getLength());
        bf.putShort(msgType.getId());
        bf.putInt(msgId);
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

        bf.put(isEOF ? (byte)1 : 0);

        data = new byte[(int) (length - HEADER_LENGTH)];
        int pos = 0;
        while (bf.hasRemaining()) {
            data[pos] = bf.get();
            pos++;
        }
    }

    @Override
    public int getLength() {
        return HEADER_LENGTH + data.length;
    }
}
