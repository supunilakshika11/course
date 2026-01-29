package com.gearrentpro.controller;

import com.gearrentpro.db.Tx;
import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.RentalQuote;
import com.gearrentpro.service.RentalService;

import java.time.LocalDate;

public class RentalController {
    private final RentalService rentalService = new RentalService();

    public RentalQuote quote(String equipmentId, int customerId, LocalDate start, LocalDate end) {
        return Tx.inTx(con -> rentalService.calculateQuote(con, equipmentId, customerId, start, end));
    }

    public int createRental(int branchId, String equipmentId, int customerId, LocalDate start, LocalDate end, Enums.PaymentStatus payStatus) {
        return Tx.inTx(con -> rentalService.createRental(con, branchId, equipmentId, customerId, start, end, payStatus));
    }

    public int convertReservation(int reservationId, Enums.PaymentStatus payStatus) {
        return Tx.inTx(con -> rentalService.convertReservationToRental(con, reservationId, payStatus));
    }

    public void refreshOverdues() {
        Tx.inTx(con -> { rentalService.refreshOverdues(con); return null; });
    }
}
