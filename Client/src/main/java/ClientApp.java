import messages.auth.AuthMessage;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        CloudClient client = new CloudClient();
        try {
            client.connect("localhost", 8780);
            String msg;
            Scanner scanner = new Scanner(System.in);
            client.send(new AuthMessage("l1", "p1", false));

            do {
                msg = scanner.nextLine();
                String[] commands = msg.split(" ");
                if (commands.length == 0) continue;

                if (commands[0].equals("/auth")) {
                    if (commands.length < 3) {
                        System.out.println("Не достаточно параметров...");
                        continue;
                    }
                    client.send(new AuthMessage(commands[1], commands[2], false));
                    continue;
                }

                if (commands[0].equals("/send")) {
                    if (commands.length < 2) {
                        System.out.println("Не достаточно параметров...");
                        continue;
                    }
                    client.sendFile(commands[1]);
                    continue;
                }

                CommandMessageType type = CommandMessageType.findByCommand(commands[0]);
                if (type == null) {
                    System.out.println("Command doesn't support: " + commands[0]);
                    continue;
                }

                CommandMessage cm = new CommandMessage(type);
                if (commands.length > 1) {
                    cm.setParameter(commands[1]);
                }
                client.send(cm);
            } while (!msg.equals("/close"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
