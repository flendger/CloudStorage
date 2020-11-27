package messages.command;

import messages.AbstractMessage;
import messages.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CommandMessage extends AbstractMessage {

    public static final int HEADER_LENGTH = 16;
    private final ByteBuffer bf = ByteBuffer.allocate(2048);

    private CommandMessageType command;
    private String parameter = "";
    private byte[] data = new byte[0];


    public CommandMessage(byte[] bytes) {
        super(MessageType.COMMAND);
        fromBytes(bytes);
    }

    public CommandMessage(CommandMessageType command) {
        super(MessageType.COMMAND);
        this.command = command;
    }

    public CommandMessage(CommandMessageType command, String parameter) {
        this(command);
        this.parameter = parameter;
    }

    @Override
    public byte[] toBytes() {
        bf.clear();

        bf.putInt(getLength());
        bf.putShort(msgType.getId());
        bf.putInt(msgId);
        bf.putShort(command.getId());
        bf.putInt(parameter.getBytes().length);
        bf.put(parameter.getBytes());
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

        command = CommandMessageType.findById(bf.getShort());

        int parLength = bf.getInt();

        byte[] parBytes = new byte[parLength];
        int pos = 0;
        while (bf.hasRemaining() && pos < parLength) {
            parBytes[pos] = bf.get();
            pos++;
        }
        parameter = new String(parBytes, StandardCharsets.UTF_8);

        data = new byte[length - HEADER_LENGTH - parLength];
        if (bf.hasRemaining()) bf.get(data);
    }

    @Override
    public int getLength() {
        return HEADER_LENGTH + parameter.getBytes().length + data.length;
    }

    public CommandMessageType getCommand() {
        return command;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommandMessage{" +
                "msgId=" + msgId +
                ", msgLength=" + getLength() +
                ", msgType=" + msgType +
                ", command=" + command +
                ", parameter='" + parameter + '\'' +
                ", data length='" + data.length + '\'' +
                '}';
    }
}
