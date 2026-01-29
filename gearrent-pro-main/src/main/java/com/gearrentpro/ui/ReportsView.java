package com.gearrentpro.ui;

import com.gearrentpro.entity.UserSession;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReportsView extends VBox {
    public ReportsView(Stage stage, UserSession session) {
        setPadding(new Insets(18));
        setSpacing(10);

        getChildren().addAll(
            new Label("Reports (coming soon)"),
            new Button("Back")
        );

        ((Button) getChildren().get(1)).setOnAction(e ->
            stage.getScene().setRoot(new MainMenuView(stage, session))
        );
    }
}
