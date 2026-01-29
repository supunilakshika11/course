package com.gearrentpro.dao;

import com.gearrentpro.db.DB;
import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.Equipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EquipmentDao {

    public Equipment findById(Connection con, String equipmentId) throws Exception {
        String sql = "SELECT * FROM equipment WHERE equipment_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, equipmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return new Equipment(
                        rs.getString("equipment_id"),
                        rs.getInt("category_id"),
                        rs.getInt("branch_id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("purchase_year"),
                        rs.getBigDecimal("base_daily_price"),
                        rs.getBigDecimal("security_deposit"),
                        Enums.EquipmentStatus.valueOf(rs.getString("status")));
            }
        }
    }

    public void updateStatus(Connection con, String equipmentId, Enums.EquipmentStatus status) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE equipment SET status=? WHERE equipment_id=?")) {
            ps.setString(1, status.name());
            ps.setString(2, equipmentId);
            if (ps.executeUpdate() != 1)
                throw new IllegalStateException("Equipment not updated: " + equipmentId);
        }
    }

    public void updateStatusIf(Connection con, String equipmentId, Enums.EquipmentStatus expected,
            Enums.EquipmentStatus next) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE equipment SET status=? WHERE equipment_id=? AND status=?")) {
            ps.setString(1, next.name());
            ps.setString(2, equipmentId);
            ps.setString(3, expected.name());
            if (ps.executeUpdate() != 1)
                throw new IllegalStateException("Equipment status changed by another user or not available.");
        }
    }

    public boolean setAvailable(String equipmentId) {
        String sql = "UPDATE equipment SET status='AVAILABLE' WHERE equipment_id=?";

        try (var con = DB.getConnection();
                var ps = con.prepareStatement(sql)) {

            ps.setString(1, equipmentId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
