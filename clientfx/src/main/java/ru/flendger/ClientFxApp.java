package ru.flendger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import clientcore.CloudClientNetty;
import ru.flendger.fxgui.Controller;

public class ClientFxApp extends Application {

    private CloudClientNetty client;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        Controller ctrl = loader.getController();

        client = new CloudClientNetty();
        ctrl.setClient(client);
        ctrl.connect();

        primaryStage.setTitle("Cloud File Manager");
        primaryStage.setScene(new Scene(root, 1280, 600));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        client.close();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
