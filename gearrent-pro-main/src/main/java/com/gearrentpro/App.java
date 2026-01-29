package com.gearrentpro;

import com.gearrentpro.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("GearRent Pro");
        stage.setScene(new Scene(new LoginView(stage), 520, 320));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
