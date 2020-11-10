import messages.Message;
import messages.MessageType;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        CloudClient client = new CloudClient();
        try {
            client.connect("localhost", 8780);
            String msg;
            Scanner scanner = new Scanner(System.in);
            do {
                Message netMsg = new Message(MessageType.MSG_GET_LS);
                netMsg.setParameter("/dir1/dir2");
                client.send(netMsg);

                netMsg.setParameter("/dir3/dir4");
                client.send(netMsg);
                msg = scanner.nextLine();
            } while (!msg.equals("/close"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
