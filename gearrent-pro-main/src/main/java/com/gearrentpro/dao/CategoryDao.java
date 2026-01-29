package com.gearrentpro.dao;

import com.gearrentpro.entity.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CategoryDao {
    public Category findById(Connection con, int id) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM category WHERE category_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Category(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("base_price_factor"),
                        rs.getBigDecimal("weekend_multiplier"),
                        rs.getBigDecimal("late_fee_per_day"),
                        rs.getBoolean("is_active")
                );
            }
        }
    }
}
