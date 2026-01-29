package com.gearrentpro.ui;

import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.UserSession;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuView extends VBox {

    public MainMenuView(Stage stage, UserSession session) {
        setPadding(new Insets(18));
        setSpacing(10);

        Label h = new Label("Main Menu (" + session.role() + ")");
        h.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button rentalBtn = new Button("Create Rental / Quote");
        Button returnBtn = new Button("Process Return");
        Button adminBranchesBtn = new Button("Manage Branches (Admin only) - placeholder");
        Button reportsBtn = new Button("Reports");
        Button maintenanceBtn = new Button("Activate Equipment (Maintenance)");

       
        adminBranchesBtn.setDisable(session.role() != Enums.Role.ADMIN);

        
        rentalBtn.setOnAction(e -> stage.getScene().setRoot(new RentalView(stage, session)));
        returnBtn.setOnAction(e -> stage.getScene().setRoot(new ReturnView(stage, session)));
        reportsBtn.setOnAction(e -> stage.getScene().setRoot(new ReportsView(stage, session)));
        maintenanceBtn.setOnAction(e -> stage.getScene().setRoot(new MaintenanceView(stage, session)));

        
        getChildren().addAll(
                h,
                rentalBtn,
                returnBtn,
                adminBranchesBtn,
                reportsBtn,
                maintenanceBtn,
                new Label("Note: Add more screens (Equipment/Category/Customer/Reservation/Reports) similarly.")
        );
    }
}
