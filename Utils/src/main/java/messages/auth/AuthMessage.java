package messages.auth;

import messages.AbstractMessage;
import messages.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AuthMessage extends AbstractMessage {

    public static final int HEADER_LENGTH = 11;
    private final ByteBuffer bf = ByteBuffer.allocate(2048);

    private String user;
    private String pass;
    private boolean isRegister;
    private String parameter;

    public AuthMessage(byte[] bytes) {
        super(MessageType.AUTH);
        fromBytes(bytes);
    }

    public AuthMessage() {
        super(MessageType.AUTH);
    }

    @Override
    public byte[] toBytes() {
        bf.clear();

        bf.putInt(getLength());
        bf.putShort(msgType.getId());
        bf.putInt(msgId);
        bf.put((isRegister ? (byte)1 : 0));
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

        isRegister = bf.get() == 1;

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

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
