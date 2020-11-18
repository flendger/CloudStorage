import files.FileList;
import messages.AbstractMessage;
import messages.Message;
import messages.MessageUtils;
import messages.command.CommandMessage;
import messages.dataTransfer.DataTransferMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;

public class CloudClient {

    private final Socket socket = new Socket();
    private DataOutputStream out;

    private byte[] readBuf;
    private byte[] writeBuf;


    public void connect(String host, int port) throws IOException {
        socket.connect(new InetSocketAddress(host, port));
        try {
            out = new DataOutputStream(socket.getOutputStream());

            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())){
            while (true) {
                int length = in.readInt();
                if (length == 0) continue;

                readBuf = Arrays.copyOf(ByteBuffer.allocate(4).putInt(length).array(), length);
                in.read(readBuf, 4, length - 4);
                Message msgObject = MessageUtils.getMessageFromBytes(readBuf);
                String msg = msgObject.toString();
                System.out.println(msg);

                if (msgObject instanceof CommandMessage) {
                    CommandMessage comMsg = (CommandMessage) msgObject;
                    switch (comMsg.getCommand()) {
                        case MSG_PUT_LS:
                            if (comMsg.getData().length > 0) {
                                FileList fl = (FileList) MessageUtils.BytesToObject(comMsg.getData());
                                System.out.println(fl);
                            }
                            break;
                    }
                } else if (msgObject instanceof DataTransferMessage) {
                    System.out.println("Received file...");
                }
                if (msg.contains("/close")) {
                    System.out.println("Bye...");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <M extends AbstractMessage> void send(M msg) {
        send(msg.toBytes());
    }

    public void send(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: move sending to FileUtils
    //TODO: add collecting file form messages
    public void sendFile(String filePath) throws IOException{
        Path path = Path.of(filePath);
        if (! Files.exists(path)) throw new NoSuchFileException("File doesn't exist: " + path.toString());
        if (Files.isDirectory(path)) throw new NoSuchFileException("File is directory: " + path.toString());

        File file = new File(filePath);
        FileInputStream fileBytes = new FileInputStream(file);
        writeBuf = new byte[DataTransferMessage.MAX_DATA_BUFFER_SIZE];

        int fileId = DataTransferMessage.generateFileId();

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

            send(msg);
        }
    }

    public void close() throws IOException {
        out.close();
        socket.close();
    }
}
