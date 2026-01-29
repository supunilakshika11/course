package com.gearrentpro.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConfigDao {
    public BigDecimal getDecimal(Connection con, String key) throws Exception {
        try (PreparedStatement ps = con.prepareStatement("SELECT config_value FROM app_config WHERE config_key=?")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("Missing config: " + key);
                return new BigDecimal(rs.getString(1));
            }
        }
    }
}
