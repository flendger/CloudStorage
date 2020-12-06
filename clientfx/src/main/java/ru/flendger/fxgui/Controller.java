package ru.flendger.fxgui;

import clientcore.CloudClientNetty;
import files.FileList;
import files.FileTransferRecord;
import files.FileUtils;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import messages.Message;
import messages.MessageUtils;
import messages.auth.AuthMessage;
import messages.command.CommandMessage;
import messages.command.CommandMessageType;
import messages.dataTransfer.DataTransferMessage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class Controller {

    private CloudClientNetty client;
    private final ConcurrentHashMap<Integer, FileTransferRecord> incomingFiles = new ConcurrentHashMap<>();
    private String user;
    private String pass;

    @FXML
    VBox leftPanel, rightPanel;


    public void setClient(CloudClientNetty client) {
        this.client = client;
        rightPanel.getProperties().put("client", client);
    }

    public void connect() {
        if (client == null) return;

        try {
            client.setReadable(this::read);
            new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            client.connect("localhost", 8780);
                            return null;
                        }
                    };
                }
            }.restart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read(Message msg) {
        System.out.println(msg);

        if (msg instanceof CommandMessage) {
            CommandMessage comMsg = (CommandMessage) msg;
            switch (comMsg.getCommand()) {
                case MSG_PUT_LS:
                    if (comMsg.getData().length > 0) {
                        FileList fl = (FileList) MessageUtils.BytesToObject(comMsg.getData());
                        RemotePanelController rightPC = (RemotePanelController) rightPanel.getProperties().get("ctrl");
                        Platform.runLater(() -> rightPC.updateList(comMsg.getParameter(), fl));
                        System.out.println(fl.toString());
                    }
                    break;
                case MSG_AUTH_OK:
                    client.send(MessageUtils.getLsMessage());
                    break;
                case MSG_AUTH_ERROR:
                case MSG_REG_ERROR:
                    FxUtils.showErr(comMsg.getParameter());
                    break;
                case MSG_REG_OK:
                    client.send(new AuthMessage(user, pass, false));
                    break;
            }
        } else if (msg instanceof DataTransferMessage) {
            try {
                DataTransferMessage dMsg = (DataTransferMessage) msg;
                getFilePart(dMsg);
                if (dMsg.isEOF()) {
                    Platform.runLater(() -> ((PanelController) leftPanel.getProperties().get("ctrl")).update());
                    FxUtils.showOk(String.format("File [%s] download complete", dMsg.getFileName()));
                }
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
            PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
            String curDir = leftPC.getCurrentPath();
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

    public void btnExitAction() {
        Platform.exit();
    }

    public void uploadBtnAction() {
        if (client.getChannel() == null) {
            FxUtils.showErr("Connection to server failed");
            return;
        }

        PanelController srcPC = (PanelController) leftPanel.getProperties().get("ctrl");
        if (srcPC.getSelectedFilename() == null) {
            FxUtils.showErr("File not selected");
            return;
        }

        try {
            client.sendFile(Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFilename()).toString());
            client.send(new CommandMessage(CommandMessageType.MSG_GET_LS));
        } catch (IOException e) {
            e.printStackTrace();
            FxUtils.showErr(e.getMessage());
        }
    }

    public void refreshBtnAction() {
        if (client.getChannel() == null) {
            FxUtils.showErr("Connection to server failed");
            return;
        }

        client.send(new CommandMessage(CommandMessageType.MSG_GET_LS));
    }

    public void downloadBtnAction() {
        if (client.getChannel() == null) {
            FxUtils.showErr("Connection to server failed");
            return;
        }

        RemotePanelController srcPC = (RemotePanelController) rightPanel.getProperties().get("ctrl");
        if (srcPC.getSelectedFilename() == null) {
            FxUtils.showErr("File not selected");
            return;
        }

        client.send(new CommandMessage(
                CommandMessageType.MSG_GET_FILE,
                srcPC.getSelectedFilename()
        ));
    }

    public void btnConnect() {
        AuthMessage msg = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ru/flendger/auth.fxml"));
            Parent root = fxmlLoader.load();
            AuthController ctrl = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            msg = ctrl.getMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (msg == null) return;

        user = msg.getUser();
        pass = msg.getPass();

        if (client.getChannel() == null) {
            connect();
        }
        for (int i = 0; i < 3; i++) {
            if (client.getChannel() != null) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (client.getChannel() == null) {
            FxUtils.showErr("Connection to server failed");
        } else {
            client.send(msg);
            client.send(MessageUtils.getLsMessage());
        }
    }
}