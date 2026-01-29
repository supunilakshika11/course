package com.gearrentpro.dao;

import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.RentalQuote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class RentalDao {

    public int insertRental(Connection con,
                            String equipmentId,
                            int customerId,
                            int branchId,
                            LocalDate start,
                            LocalDate end,
                            RentalQuote q,
                            Enums.PaymentStatus payStatus,
                            Integer reservationId) throws Exception {

        String sql = """
            INSERT INTO rental(equipment_id,customer_id,branch_id,start_date,end_date,
                               rental_amount,security_deposit,membership_discount,long_rental_discount,final_payable,
                               payment_status,rental_status,reservation_id)
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, equipmentId);
            ps.setInt(2, customerId);
            ps.setInt(3, branchId);
            ps.setDate(4, java.sql.Date.valueOf(start));
            ps.setDate(5, java.sql.Date.valueOf(end));

            ps.setBigDecimal(6, q.rentalAmount());
            ps.setBigDecimal(7, q.securityDeposit());
            ps.setBigDecimal(8, q.membershipDiscount());
            ps.setBigDecimal(9, q.longRentalDiscount());
            ps.setBigDecimal(10, q.finalPayable());

            ps.setString(11, payStatus.name());
            ps.setString(12, Enums.RentalStatus.ACTIVE.name());
            if (reservationId == null) ps.setObject(13, null);
            else ps.setInt(13, reservationId);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void markOverdue(Connection con) throws Exception {
        // current_date > end_date and not returned/cancelled
        try (PreparedStatement ps = con.prepareStatement("""
            UPDATE rental
            SET rental_status='OVERDUE'
            WHERE rental_status='ACTIVE' AND CURDATE() > end_date
        """)) {
            ps.executeUpdate();
        }
    }

    public RentalRow findRental(Connection con, int rentalId) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM rental WHERE rental_id=?")) {
            ps.setInt(1, rentalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new RentalRow(
                        rs.getInt("rental_id"),
                        rs.getString("equipment_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("branch_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getBigDecimal("security_deposit"),
                        Enums.RentalStatus.valueOf(rs.getString("rental_status"))
                );
            }
        }
    }

    public void updateStatus(Connection con, int rentalId, Enums.RentalStatus st) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("UPDATE rental SET rental_status=? WHERE rental_id=?")) {
            ps.setString(1, st.name());
            ps.setInt(2, rentalId);
            if (ps.executeUpdate() != 1) throw new IllegalStateException("Rental not updated");
        }
    }

    public record RentalRow(
            int rentalId,
            String equipmentId,
            int customerId,
            int branchId,
            LocalDate start,
            LocalDate end,
            java.math.BigDecimal deposit,
            Enums.RentalStatus status
    ) {}
}
