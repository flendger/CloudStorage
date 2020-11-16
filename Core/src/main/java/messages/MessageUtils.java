package messages;

import messages.auth.AuthMessage;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import messages.dataTransfer.DataTransferMessage;

import java.io.*;

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

    public static byte[] ObjectToBytes(Object o) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(bos)){
            outputStream.writeObject(o);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Object BytesToObject(byte[] bytes) {
        Object res = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream inputStream = new ObjectInputStream(bis)){
            res = inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
