package ru.flendger.fxgui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class FxUtils {
    public static void showErr(String txt) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, txt, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public static void showOk(String txt) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, txt, ButtonType.OK);
            alert.showAndWait();
        });
    }
}
