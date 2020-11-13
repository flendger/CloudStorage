package messages;

import java.time.LocalTime;

/**
 * Message protocol:
 *      msg_length - [long] header (15 bytes) + data (non-fix)
 *      msg_type_id = [short] message command (id)
 *      msg_id - [integer] message id
 *      data = [ byte[] ] (if exists) transferring data (file, command, etc) - specific for message type
 */

public abstract class AbstractMessage implements Message{

    protected MessageType msgType;
    protected int msgId;

    protected AbstractMessage(MessageType msgType) {
        this.msgType = msgType;
        this.msgId = generateId();
    }

    protected int generateId() {
        return (int) (Math.random() * 100000);
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s]", msgId, msgType.toString());
    }
}
