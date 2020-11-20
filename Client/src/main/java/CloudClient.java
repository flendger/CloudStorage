import files.FileList;
import files.FileTransferRecord;
import files.FileUtils;
import messages.Message;
import messages.MessageUtils;
import messages.command.CommandMessage;
import messages.dataTransfer.DataTransferMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class CloudClient {

    private final Socket socket = new Socket();
    private final ConcurrentHashMap<Integer, FileTransferRecord> incomingFiles;
    private DataOutputStream out;

    public CloudClient() {
        this.incomingFiles = new ConcurrentHashMap<>();
    }


    private void read() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())){
            while (true) {
                int length = in.readInt();
                if (length == 0) continue;

                byte[] readBuf = Arrays.copyOf(ByteBuffer.allocate(4).putInt(length).array(), length);
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
                    getFilePart((DataTransferMessage) msgObject);
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

    private void getFilePart(DataTransferMessage msg) throws IOException {
        //isFirst -> create tmp file -> add record to active file list
        //record:
        // fileId - msg.fileId = Key
        // fullTmpName - "tmp" + fileId + generateId
        // destDir - curDir
        // fileName - msg.fileName
        if (msg.isFirst()) {
            //TODO: add user profile, current dir etc...
            String curDir = Path.of("ClientStorage").toAbsolutePath().toString();
            String tmp = FileUtils.createTmpFile(msg.getFileId(), curDir);

            incomingFiles.put(msg.getFileId(),
                    new FileTransferRecord(msg.getFileId(), msg.getFileName(), tmp, curDir));
        }

        //is file in list -> write part or ignore
        FileTransferRecord rec = incomingFiles.get(msg.getFileId());
        if (rec == null) {
            return;
        }
        FileUtils.writeDataToFile(rec.tmpFileName, msg.getData());

        //EOF -> rename file + remove from list or ignore
        if (! msg.isEOF()) {
            return;
        }
        incomingFiles.remove(rec.fileId);
        FileUtils.renameTmpToFile(rec.tmpFileName, rec.dir, rec.fileName);
    }

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

    public <M extends Message> void send(M msg) {
        send(msg.toBytes());
    }

    public void send(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String filePath) throws IOException{
        MessageUtils.sendFile(filePath, this::send);
    }

    public void close() throws IOException {
        out.close();
        socket.close();
    }
}
