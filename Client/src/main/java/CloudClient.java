import messages.Message;

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
                        String msg = Message.of(Arrays.copyOf(buf, cnt)).toString();
                        System.out.println(msg);
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

    public void send(Message msg) {
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
