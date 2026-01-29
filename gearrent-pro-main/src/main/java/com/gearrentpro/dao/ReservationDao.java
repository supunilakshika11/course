package com.gearrentpro.dao;

import com.gearrentpro.entity.Enums;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ReservationDao {

    public int insert(Connection con, String equipmentId, int customerId, int branchId, LocalDate start, LocalDate end) throws Exception {
        String sql = "INSERT INTO reservation(equipment_id,customer_id,branch_id,start_date,end_date,status) VALUES(?,?,?,?,?,'ACTIVE')";
        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, equipmentId);
            ps.setInt(2, customerId);
            ps.setInt(3, branchId);
            ps.setDate(4, java.sql.Date.valueOf(start));
            ps.setDate(5, java.sql.Date.valueOf(end));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public ResRow find(Connection con, int reservationId) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM reservation WHERE reservation_id=?")) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new ResRow(
                        rs.getInt("reservation_id"),
                        rs.getString("equipment_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("branch_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        Enums.ReservationStatus.valueOf(rs.getString("status"))
                );
            }
        }
    }

    public void updateStatus(Connection con, int reservationId, Enums.ReservationStatus st) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("UPDATE reservation SET status=? WHERE reservation_id=?")) {
            ps.setString(1, st.name());
            ps.setInt(2, reservationId);
            if (ps.executeUpdate() != 1) throw new IllegalStateException("Reservation not updated");
        }
    }

    public record ResRow(int id, String equipmentId, int customerId, int branchId, LocalDate start, LocalDate end, Enums.ReservationStatus status) {}
}
