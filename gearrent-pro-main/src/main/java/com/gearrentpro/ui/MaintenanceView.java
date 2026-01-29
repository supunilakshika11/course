package com.gearrentpro.ui;

import com.gearrentpro.dao.EquipmentDao;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MaintenanceView extends VBox {

    public MaintenanceView(Stage stage, Object session) {

        setPadding(new Insets(20));
        setSpacing(10);

        Label title = new Label("Activate Equipment from Maintenance");

        TextField equipmentIdField = new TextField();
        equipmentIdField.setPromptText("Equipment ID (ex: CAM_003)");

        Button activateBtn = new Button("Set ACTIVE");

        Label msg = new Label();

        activateBtn.setOnAction(e -> {
            String id = equipmentIdField.getText();

            if (id.isEmpty()) {
                msg.setText("Add Equipment ID");
                return;
            }

            boolean ok = new EquipmentDao().setAvailable(id);

            msg.setText(ok
                ? "Equipment ACTIVE ✅"
                : "Error: Equipment not found ❌");
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e ->
            stage.getScene().setRoot(new MainMenuView(stage, (com.gearrentpro.entity.UserSession) session))
        );

        getChildren().addAll(title, equipmentIdField, activateBtn, msg, backBtn);
    }
}

