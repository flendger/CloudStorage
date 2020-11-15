package messages.command;

import messages.AbstractMessage;
import messages.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CommandMessage extends AbstractMessage {

    public static final int HEADER_LENGTH = 12;
    private final ByteBuffer bf = ByteBuffer.allocate(2048);

    private CommandMessageType command;
    private String parameter = "";

    public CommandMessage(byte[] bytes) {
        super(MessageType.COMMAND);
        fromBytes(bytes);
    }

    public CommandMessage(CommandMessageType command) {
        super(MessageType.COMMAND);
        this.command = command;
    }

    @Override
    public byte[] toBytes() {
        bf.clear();

        bf.putInt(getLength());
        bf.putShort(msgType.getId());
        bf.putInt(msgId);
        bf.putShort(command.getId());
        bf.put(parameter.getBytes());

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

        byte[] parBytes = new byte[(int) (length - HEADER_LENGTH)];
        int pos = 0;
        while (bf.hasRemaining()) {
            parBytes[pos] = bf.get();
            pos++;
        }
        parameter = new String(parBytes, StandardCharsets.UTF_8);
    }

    @Override
    public int getLength() {
        return HEADER_LENGTH + parameter.getBytes().length;
    }

    public CommandMessageType getCommand() {
        return command;
    }

    public void setCommand(CommandMessageType command) {
        this.command = command;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "CommandMessage{" +
                "msgId=" + msgId +
                ", msgLength=" + getLength() +
                ", msgType=" + msgType +
                ", command=" + command +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
