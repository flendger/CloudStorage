import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class CloudClient {

    private Socket socket = new Socket();
    private DataInputStream in;
    private DataOutputStream out;


    public void connect(String host, int port) throws IOException {
        socket.connect(new InetSocketAddress(host, port));
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                while (socket.isConnected() && !socket.isClosed()) {
                    try {
                        String msg = "";
                        byte[] buffer = new byte[256];
                        int cnt = in.read(buffer);
                        in.readUTF();
                        msg = new String(buffer, Charset.defaultCharset());
                        System.out.println(msg);
                        if (msg.contains("/close")) {
                            System.out.println("hello");
                            break;
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
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
