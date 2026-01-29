package com.gearrentpro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class AvailabilityDao {

 
    public boolean hasOverlap(Connection con, String equipmentId, LocalDate start, LocalDate end) throws Exception {
        String sql = """
        SELECT
          (SELECT COUNT(*) FROM reservation
             WHERE equipment_id=? AND status='ACTIVE'
               AND NOT (end_date < ? OR start_date > ?)
          ) +
          (SELECT COUNT(*) FROM rental
             WHERE equipment_id=? AND rental_status IN ('ACTIVE','OVERDUE')
               AND NOT (end_date < ? OR start_date > ?)
          ) AS c
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, equipmentId);
            ps.setDate(2, java.sql.Date.valueOf(start));
            ps.setDate(3, java.sql.Date.valueOf(end));

            ps.setString(4, equipmentId);
            ps.setDate(5, java.sql.Date.valueOf(start));
            ps.setDate(6, java.sql.Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("c") > 0;
            }
        }
    }
}
