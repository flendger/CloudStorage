package messages.auth;

import messages.AbstractMessage;
import messages.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AuthMessage extends AbstractMessage {

    public static final int HEADER_LENGTH = 11;
    private final ByteBuffer bf = ByteBuffer.allocate(2048);

    private String user = "";
    private String pass = "";
    private boolean isRegistration;

    public AuthMessage(byte[] bytes) {
        super(MessageType.AUTH);
        fromBytes(bytes);
    }

    public AuthMessage() {
        super(MessageType.AUTH);
    }

    public AuthMessage(String user, String pass, boolean isRegistration) {
        this();
        this.user = user;
        this.pass = pass;
        this.isRegistration = isRegistration;
    }

    @Override
    public byte[] toBytes() {
        bf.clear();

        bf.putInt(getLength());
        bf.putShort(msgType.getId());
        bf.putInt(msgId);
        bf.put((isRegistration ? (byte)1 : 0));
        bf.put(getParameter().getBytes());

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

        isRegistration = bf.get() == 1;

        byte[] parBytes = new byte[(int) (length - HEADER_LENGTH)];
        int pos = 0;
        while (bf.hasRemaining()) {
            parBytes[pos] = bf.get();
            pos++;
        }
        setUserPassFormParameter(new String(parBytes, StandardCharsets.UTF_8));
    }

    @Override
    public int getLength() {
        return HEADER_LENGTH + getParameter().getBytes().length;
    }

    public String getParameter() {
        return (user.trim() + ";" + pass.trim());
    }

    private void setUserPassFormParameter(String parameter) {
        String[] params = parameter.split(";");
        if (params.length == 0) return;

        this.user = params[0].trim();

        if (params.length > 1) {
            this.pass = params[1];
        }
    }

    public boolean isRegistration() {
        return isRegistration;
    }

    public void setRegistration(boolean registration) {
        isRegistration = registration;
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

    @Override
    public String toString() {
        return "AuthMessage{" +
                "msgId=" + msgId +
                ", msgLength=" + getLength() +
                ", msgType=" + msgType +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", isRegister=" + isRegistration +
                '}';
    }
}
