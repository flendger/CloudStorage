import java.io.IOException;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        CloudClient client = new CloudClient();
        try {
            client.connect("localhost", 8780);
            String msg = "";
            Scanner scanner = new Scanner(System.in);
            do {
                msg = scanner.nextLine();
                client.send(msg);
            } while (!msg.equals("/close"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
