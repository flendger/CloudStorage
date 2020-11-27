package cli;

import clientcore.CloudClientIO;
import files.FileList;
import files.FileTransferRecord;
import files.FileUtils;
import messages.Message;
import messages.MessageUtils;
import messages.auth.AuthMessage;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import messages.dataTransfer.DataTransferMessage;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ClientCLI {

    private final ConcurrentHashMap<Integer, FileTransferRecord> incomingFiles;

    public ClientCLI() {
        this.incomingFiles = new ConcurrentHashMap<>();

        CloudClientIO client = new CloudClientIO(this::read);
        try {
            client.connect("localhost", 8780);

            Thread thr = new Thread(client::read);
            thr.setDaemon(true);
            thr.start();

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

    private synchronized void read(Message msg) {
        System.out.println(msg);

        if (msg instanceof CommandMessage) {
            CommandMessage comMsg = (CommandMessage) msg;
            switch (comMsg.getCommand()) {
                case MSG_PUT_LS:
                    if (comMsg.getData().length > 0) {
                        FileList fl = (FileList) MessageUtils.BytesToObject(comMsg.getData());
                        System.out.println(fl);
                    }
                    break;
            }
        } else if (msg instanceof DataTransferMessage) {
            try {
                getFilePart((DataTransferMessage) msg);
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
            String curDir = FileUtils.getFullPath("ClientStorage");
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
}
