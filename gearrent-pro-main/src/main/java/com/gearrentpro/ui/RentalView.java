package com.gearrentpro.ui;

import com.gearrentpro.controller.RentalController;
import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.RentalQuote;
import com.gearrentpro.entity.UserSession;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RentalView extends VBox {
    private final RentalController controller = new RentalController();

    public RentalView(Stage stage, UserSession session) {
        setPadding(new Insets(18));
        setSpacing(10);

        Label h = new Label("Rental - Quote & Create");
        h.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);

        TextField equipmentId = new TextField(); equipmentId.setPromptText("e.g. CAM-001");
        TextField customerId = new TextField(); customerId.setPromptText("e.g. 1");

        DatePicker start = new DatePicker(LocalDate.now());
        DatePicker end = new DatePicker(LocalDate.now().plusDays(1));

        ComboBox<Enums.PaymentStatus> pay = new ComboBox<>();
        pay.getItems().addAll(Enums.PaymentStatus.PAID, Enums.PaymentStatus.UNPAID, Enums.PaymentStatus.PARTIALLY_PAID);
        pay.setValue(Enums.PaymentStatus.UNPAID);

        TextArea out = new TextArea();
        out.setEditable(false);
        out.setPrefRowCount(8);

        Button quoteBtn = new Button("Calculate Quote");
        Button createBtn = new Button("Create Rental");
        Button back = new Button("Back");

        quoteBtn.setOnAction(e -> {
            try {
                RentalQuote q = controller.quote(equipmentId.getText().trim(), Integer.parseInt(customerId.getText().trim()),
                        start.getValue(), end.getValue());
                out.setText("""
                        Rental Amount: %s
                        Membership Discount: %s
                        Long Rental Discount: %s
                        Final Payable: %s
                        Security Deposit: %s
                        """.formatted(q.rentalAmount(), q.membershipDiscount(), q.longRentalDiscount(), q.finalPayable(), q.securityDeposit()));
            } catch (Exception ex) {
                out.setText("ERROR: " + ex.getMessage());
            }
        });

        createBtn.setOnAction(e -> {
            try {
                Integer branchId = session.branchId();
                if (session.role() == Enums.Role.ADMIN && branchId == null) {
                  
                    out.setText("Admin demo: Please login as branch user (pan_mgr / gal_staff) to issue rentals.");
                    return;
                }
                int rid = controller.createRental(branchId, equipmentId.getText().trim(), Integer.parseInt(customerId.getText().trim()),
                        start.getValue(), end.getValue(), pay.getValue());
                out.setText("Rental created! Rental ID = " + rid);
            } catch (Exception ex) {
                out.setText("ERROR: " + ex.getMessage());
            }
        });

        back.setOnAction(e -> stage.getScene().setRoot(new MainMenuView(stage, session)));

        g.addRow(0, new Label("Equipment ID"), equipmentId);
        g.addRow(1, new Label("Customer ID"), customerId);
        g.addRow(2, new Label("Start Date"), start);
        g.addRow(3, new Label("End Date"), end);
        g.addRow(4, new Label("Payment Status"), pay);

        getChildren().addAll(h, g, quoteBtn, createBtn, out, back);
    }
}
