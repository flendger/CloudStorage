package messages;

import files.FileUtils;
import messages.auth.AuthMessage;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import messages.dataTransfer.DataTransferMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public static CommandMessage getLsMessage() {
        return new CommandMessage(CommandMessageType.MSG_GET_LS);
    }

    public static CommandMessage getCdMessage(String dir) {
        CommandMessage msg = new CommandMessage(CommandMessageType.MSG_GET_CD);
        msg.setParameter(dir);
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

    public static void sendFile(String filePath, Sendable ref) throws IOException{
        Path path = Paths.get(filePath);
        if (! Files.exists(path)) throw new NoSuchFileException("File doesn't exist: " + path.toString());
        if (Files.isDirectory(path)) throw new NoSuchFileException("File is directory: " + path.toString());

        File file = new File(filePath);
        FileInputStream fileBytes = new FileInputStream(file);
        byte[] writeBuf = new byte[DataTransferMessage.MAX_DATA_BUFFER_SIZE];

        int fileId = FileUtils.generateFileId();

        int cnt = fileBytes.read(writeBuf);
        boolean isFirst = true;

        while (cnt != -1) {
            DataTransferMessage msg = new DataTransferMessage();
            msg.setFileId(fileId);
            msg.setFileName(path.getFileName().toString());
            msg.setData(writeBuf, cnt);
            msg.setFirst(isFirst);

            cnt = fileBytes.read(writeBuf);
            if (cnt == -1) {
                msg.setEOF(true);
            }
            isFirst = false;

            ref.send(msg);
        }
    }

}
