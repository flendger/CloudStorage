package ru.flendger.fxgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import messages.auth.AuthMessage;


public class AuthController {

    private AuthMessage msg;

    @FXML
    VBox auth;

    @FXML
    TextField userField;

    @FXML
    PasswordField passField;

    public void connectBtnAction(ActionEvent actionEvent) {
        msg = new AuthMessage(userField.getText(), passField.getText(), false);
        close();
    }

    public void registerBtnAction(ActionEvent actionEvent) {
        msg = new AuthMessage(userField.getText(), passField.getText(), true);
        close();
    }

    public AuthMessage getMsg() {
        return msg;
    }

    private void close() {
        ((Stage) auth.getScene().getWindow()).close();
    }
}
