package messages;

public interface Message {
    byte[] toBytes();

    void fromBytes(byte[] bytes);

    int getLength();

    int getMsgId();

    void setMsgId(int msgId);
}
