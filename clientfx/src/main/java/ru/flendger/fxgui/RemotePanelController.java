package ru.flendger.fxgui;

import clientcore.CloudClientNetty;
import files.FileInfo;
import files.FileList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import messages.Message;
import messages.MessageUtils;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RemotePanelController implements Initializable {

    @FXML
    TableView<FileInfo> filesTable;

    @FXML
    TextField pathField;

    @FXML
    VBox rootPanel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().isDirectory() ? "D" : "F"));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        filenameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "[DIR]";
                    }
                    setText(text);
                }
            }
        });
        fileSizeColumn.setPrefWidth(120);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        filesTable.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn, fileDateColumn);
        filesTable.getSortOrder().add(fileTypeColumn);

        filesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String fName = getSelectedFilename();
                if (fName == null) return;

                sendMsg(MessageUtils.getCdMessage(fName));
                sendMsg(MessageUtils.getLsMessage());

            }
        });

        rootPanel.getProperties().put("ctrl", this);
    }

    public void updateList(String curDir, FileList fileList) {
            pathField.setText(curDir);
            filesTable.getItems().clear();
            filesTable.getItems().addAll(fileList.getFileList());
            filesTable.sort();
    }

    public void btnPathUpAction() {
        if (!rootPanel.getProperties().containsKey("client")) {
            return;
        }
        sendMsg(MessageUtils.getCdMessage(".."));
        sendMsg(MessageUtils.getLsMessage());
    }

    private void sendMsg(Message msg) {
        CloudClientNetty client = (CloudClientNetty) rootPanel.getProperties().get("client");
        if (client == null) {
            FxUtils.showErr("Can't send command to server: client not found");
        }
        client.send(msg);
    }

    public String getSelectedFilename() {
        if (!filesTable.isFocused()) return null;

        final FileInfo item = filesTable.getSelectionModel().getSelectedItem();
        if (item == null) return null;

        return item.getName();
    }
}
