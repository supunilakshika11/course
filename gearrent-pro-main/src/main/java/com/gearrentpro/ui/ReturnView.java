package com.gearrentpro.ui;

import com.gearrentpro.controller.ReturnController;
import com.gearrentpro.entity.UserSession;
import com.gearrentpro.service.ReturnService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturnView extends VBox {

    private final ReturnController controller = new ReturnController();

    
    private ReturnService.Settlement lastPreview = null;

    public ReturnView(Stage stage, UserSession session) {
        setPadding(new Insets(18));
        setSpacing(10);

        Label h = new Label("Return & Settlement");
        h.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);

        TextField rentalId = new TextField();
        rentalId.setPromptText("e.g. 1");

        DatePicker actual = new DatePicker(LocalDate.now());

        TextField damageDesc = new TextField();
        damageDesc.setPromptText("optional");

        TextField damageCharge = new TextField();
        damageCharge.setPromptText("0.00");

        TextArea out = new TextArea();
        out.setEditable(false);
        out.setPrefRowCount(10);

        Button btnProcess = new Button("Process Return (Preview)");
        Button btnConfirm = new Button("Confirm Return");
        btnConfirm.setDisable(true); // ✅ disabled until preview success

        Button back = new Button("Back");

        // ---------- UI Layout ----------
        g.addRow(0, new Label("Rental ID"), rentalId);
        g.addRow(1, new Label("Actual Return Date"), actual);
        g.addRow(2, new Label("Damage Description"), damageDesc);
        g.addRow(3, new Label("Damage Charge"), damageCharge);

        getChildren().addAll(h, g, btnProcess, btnConfirm, out, back);

        // ---------- Actions ----------
        btnProcess.setOnAction(e -> {
            try {
                int rid = Integer.parseInt(rentalId.getText().trim());

                BigDecimal dc = damageCharge.getText().trim().isEmpty()
                        ? BigDecimal.ZERO
                        : new BigDecimal(damageCharge.getText().trim());

                String desc = damageDesc.getText().trim().isEmpty()
                        ? null
                        : damageDesc.getText().trim();

               
                lastPreview = controller.previewReturn(rid, actual.getValue(), desc, dc);

                out.setText("""
                        === Settlement Preview ===
                        Late days: %d
                        Late fee: %s
                        Damage charge: %s
                        Total charges: %s
                        Refund: %s
                        Additional pay: %s
                        """.formatted(
                        lastPreview.lateDays(),
                        lastPreview.lateFee(),
                        lastPreview.damageCharge(),
                        lastPreview.totalCharges(),
                        lastPreview.refund(),
                        lastPreview.additionalPay()
                ));

                btnConfirm.setDisable(false);

            } catch (Exception ex) {
                lastPreview = null;
                btnConfirm.setDisable(true);
                out.setText("ERROR: " + ex.getMessage());
            }
        });

        btnConfirm.setOnAction(e -> {
            try {
                if (lastPreview == null) {
                    out.setText("Please click Process Return (Preview) first.");
                    return;
                }

                
                controller.confirmReturn(lastPreview);

                out.appendText("\n✅ Return saved successfully!");
                btnConfirm.setDisable(true);
                lastPreview = null;

            } catch (Exception ex) {
                out.setText("ERROR: " + ex.getMessage());
            }
        });

        back.setOnAction(e -> stage.getScene().setRoot(new MainMenuView(stage, session)));
    }
}
