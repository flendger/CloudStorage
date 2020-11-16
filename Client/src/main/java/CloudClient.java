import files.FileList;
import messages.AbstractMessage;
import messages.Message;
import messages.MessageUtils;
import messages.command.CommandMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class CloudClient {

    private final Socket socket = new Socket();
    private DataOutputStream out;

    private final byte[] buf = new byte[2048];


    public void connect(String host, int port) throws IOException {
        socket.connect(new InetSocketAddress(host, port));
        try {
            out = new DataOutputStream(socket.getOutputStream());

            Thread readThread = new Thread(() -> {
                try (DataInputStream in = new DataInputStream(socket.getInputStream())){
                    while (true) {
                        int cnt = in.read(buf);
                        Message msgObject = MessageUtils.getMessageFromBytes(Arrays.copyOf(buf, cnt));
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
            });
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
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

    public void close() throws IOException {
        out.close();
        socket.close();
    }
}
