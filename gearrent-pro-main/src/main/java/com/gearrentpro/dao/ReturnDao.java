package com.gearrentpro.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class ReturnDao {
    public void insertReturn(Connection con, int rentalId, LocalDate actualReturn,
                             String damageDesc, BigDecimal damageCharge,
                             int lateDays, BigDecimal lateFee,
                             BigDecimal totalCharges, BigDecimal refund, BigDecimal addPay) throws Exception {

        String sql = """
            INSERT INTO rental_return(rental_id,actual_return_date,damage_description,damage_charge,
                                     late_days,late_fee,total_charges,refund_amount,additional_pay)
            VALUES(?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            ps.setDate(2, java.sql.Date.valueOf(actualReturn));
            ps.setString(3, damageDesc);
            ps.setBigDecimal(4, damageCharge);
            ps.setInt(5, lateDays);
            ps.setBigDecimal(6, lateFee);
            ps.setBigDecimal(7, totalCharges);
            ps.setBigDecimal(8, refund);
            ps.setBigDecimal(9, addPay);
            ps.executeUpdate();
        }
    }
}
