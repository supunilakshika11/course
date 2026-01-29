package com.gearrentpro.service;

import com.gearrentpro.dao.CategoryDao;
import com.gearrentpro.dao.EquipmentDao;
import com.gearrentpro.dao.RentalDao;
import com.gearrentpro.dao.ReturnDao;
import com.gearrentpro.db.Tx;
import com.gearrentpro.entity.Category;
import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.Equipment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnService {
    private final RentalDao rentalDao = new RentalDao();
    private final EquipmentDao equipmentDao = new EquipmentDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private final ReturnDao returnDao = new ReturnDao();

    
    public Settlement previewSettlement(int rentalId,
            LocalDate actualReturnDate,
            String damageDesc,
            BigDecimal damageCharge) throws Exception {
        return Tx.inTx(con -> computeSettlement(con, rentalId, actualReturnDate, damageDesc, damageCharge));
    }

    
    public void finalizeReturn(Settlement s) throws Exception {
        Validation.require(s != null, "Settlement required");

        Tx.inTx(con -> {
            persistReturn(con, s);
            return null;
        });
    }

    
    public Settlement processReturn(Connection con,
            int rentalId,
            LocalDate actualReturnDate,
            String damageDesc,
            BigDecimal damageCharge) throws Exception {
        Settlement s = computeSettlement(con, rentalId, actualReturnDate, damageDesc, damageCharge);
        persistReturn(con, s);
        return s;
    }

    
    private Settlement computeSettlement(Connection con,
            int rentalId,
            LocalDate actualReturnDate,
            String damageDesc,
            BigDecimal damageCharge) throws Exception {

        Validation.require(actualReturnDate != null, "Actual return date required");
        if (damageCharge == null)
            damageCharge = BigDecimal.ZERO;

        var r = rentalDao.findRental(con, rentalId);
        Validation.require(r != null, "Invalid rental");
        Validation.require(
                r.status() == Enums.RentalStatus.ACTIVE || r.status() == Enums.RentalStatus.OVERDUE,
                "Rental is not active/overdue");

        Equipment eq = equipmentDao.findById(con, r.equipmentId());
        Validation.require(eq != null, "Equipment not found");

        Category cat = categoryDao.findById(con, eq.categoryId());
        Validation.require(cat != null, "Category not found");

        int lateDays = 0;
        if (actualReturnDate.isAfter(r.end())) {
            lateDays = (int) ChronoUnit.DAYS.between(r.end(), actualReturnDate);
        }

        BigDecimal lateFee = cat.lateFeePerDay()
                .multiply(BigDecimal.valueOf(lateDays))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal dc = damageCharge.setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalCharges = lateFee.add(dc).setScale(2, RoundingMode.HALF_UP);

        BigDecimal deposit = r.deposit();
        BigDecimal refund = BigDecimal.ZERO;
        BigDecimal addPay = BigDecimal.ZERO;

        int cmp = deposit.compareTo(totalCharges);
        if (cmp > 0)
            refund = deposit.subtract(totalCharges).setScale(2, RoundingMode.HALF_UP);
        else if (cmp < 0)
            addPay = totalCharges.subtract(deposit).setScale(2, RoundingMode.HALF_UP);

        return new Settlement(
                rentalId,
                eq.equipmentId(),
                actualReturnDate,
                damageDesc,
                dc,
                lateDays,
                lateFee,
                totalCharges,
                refund,
                addPay);
    }

    
    private void persistReturn(Connection con, Settlement s) throws Exception {

        // save return row
        returnDao.insertReturn(
                con,
                s.rentalId(),
                s.actualReturnDate(),
                s.damageDesc(),
                s.damageCharge(),
                s.lateDays(),
                s.lateFee(),
                s.totalCharges(),
                s.refund(),
                s.additionalPay());

        // update rental status
        rentalDao.updateStatus(con, s.rentalId(), Enums.RentalStatus.RETURNED);

        // update equipment status
        if (s.damageCharge().compareTo(BigDecimal.ZERO) > 0) {
            equipmentDao.updateStatus(
                    con,
                    s.equipmentId(), // String
                    Enums.EquipmentStatus.MAINTENANCE);
        } else {
            equipmentDao.updateStatus(
                    con,
                    s.equipmentId(), // String
                    Enums.EquipmentStatus.AVAILABLE);
        }
    }

   
    public record Settlement(
            int rentalId,
            String equipmentId,
            LocalDate actualReturnDate,
            String damageDesc,
            BigDecimal damageCharge,
            int lateDays,
            BigDecimal lateFee,
            BigDecimal totalCharges,
            BigDecimal refund,
            BigDecimal additionalPay) {
    }
}
