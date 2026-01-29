package com.gearrentpro.ui;

import com.gearrentpro.controller.AuthController;
import com.gearrentpro.entity.UserSession;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends VBox {
    private final AuthController authController = new AuthController();

    public LoginView(Stage stage) {
        setPadding(new Insets(18));
        setSpacing(10);

        Label title = new Label("GearRent Pro - Login");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("username");

        PasswordField password = new PasswordField();
        password.setPromptText("password");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: #b00020;");

        Button btn = new Button("Login");
        btn.setDefaultButton(true);

        btn.setOnAction(e -> {
            try {
                UserSession session = authController.login(username.getText().trim(), password.getText());
                if (session == null) {
                    msg.setText("Invalid username/password");
                    return;
                }
                stage.getScene().setRoot(new MainMenuView(stage, session));
            } catch (Exception ex) {
                msg.setText(ex.getMessage());
            }
        });

        getChildren().addAll(title, new Label("Username"), username, new Label("Password"), password, btn, msg,
                new Label("Demo users: admin / pan_mgr / gal_staff (password: 1234)"));
    }
}
