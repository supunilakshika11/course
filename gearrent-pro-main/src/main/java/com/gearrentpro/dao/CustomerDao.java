package com.gearrentpro.dao;

import com.gearrentpro.entity.Customer;
import com.gearrentpro.entity.Enums;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDao {

    public Customer findById(Connection con, int id) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM customer WHERE customer_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("nic_passport"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("address"),
                        Enums.Membership.valueOf(rs.getString("membership"))
                );
            }
        }
    }

    public BigDecimal activeDepositsSum(Connection con, int customerId) throws Exception {
        String sql = """
            SELECT COALESCE(SUM(security_deposit),0) AS s
            FROM rental
            WHERE customer_id=? AND rental_status IN ('ACTIVE','OVERDUE')
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal("s");
            }
        }
    }

    public BigDecimal membershipDiscountPercent(Connection con, Enums.Membership membership) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT discount_percent FROM membership_level WHERE level=?")) {
            ps.setString(1, membership.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return BigDecimal.ZERO;
                return rs.getBigDecimal(1);
            }
        }
    }
}
