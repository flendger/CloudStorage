package clientcore;

import messages.Message;
import messages.MessageUtils;
import messages.Readable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class CloudClientIO {

    private final Socket socket = new Socket();
    private final Readable readable;
    private DataOutputStream out;

    public CloudClientIO(Readable readable) {
        this.readable = readable;
    }

    public void read() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())){
            while (true) {
                int length = in.readInt();
                if (length == 0) continue;

                System.out.print("len: " + length + "    ");
                byte[] readBuf = Arrays.copyOf(ByteBuffer.allocate(4).putInt(length).array(), length);

                int totalRead = 0;
                do {
                    int bytesRead = in.read(readBuf, 4 + totalRead, length - 4 - totalRead);
                    totalRead += bytesRead;
                } while (totalRead < length - 4);

                Message msgObject = MessageUtils.getMessageFromBytes(readBuf);
                readable.read(msgObject);
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

    public void connect(String host, int port) throws IOException {
        socket.connect(new InetSocketAddress(host, port));
        try {
            out = new DataOutputStream(socket.getOutputStream());
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
