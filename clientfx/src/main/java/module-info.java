module clientfx {
    requires javafx.fxml;
    requires javafx.controls;
    requires cloud.core;
    requires cloud.client;

    opens ru.flendger.fxgui;
    exports ru.flendger;
}