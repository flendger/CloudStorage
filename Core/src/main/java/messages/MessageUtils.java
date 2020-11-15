package messages;

import messages.auth.AuthMessage;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import messages.dataTransfer.DataTransferMessage;

public class MessageUtils {
    public static <T extends AbstractMessage> T getMessageFromBytes(byte[] bytes) {
        MessageHeader header = new MessageHeader(bytes);
        switch (header.msgType) {
            case AUTH:
                return (T) new AuthMessage(bytes);
            case COMMAND:
                return (T) new CommandMessage(bytes);
            case DATA_TRANSFER:
                return ((T) new DataTransferMessage(bytes));
        }
        return null;
    }

    public static CommandMessage getErrorMessage(String text) {
        CommandMessage msg = new CommandMessage(CommandMessageType.MSG_ERR);
        msg.setParameter(text);
        return msg;
    }

    public static CommandMessage getOKMessage(String text) {
        CommandMessage msg = new CommandMessage(CommandMessageType.MSG_OK);
        msg.setParameter(text);
        return msg;
    }
}
