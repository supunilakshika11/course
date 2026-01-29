package com.gearrentpro.dao;

import com.gearrentpro.entity.Enums;
import com.gearrentpro.entity.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
    public UserSession login(Connection con, String username, String password) throws Exception {
 
        String sql = "SELECT user_id, username, role, branch_id, password_hash FROM system_user WHERE username=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String passHash = rs.getString("password_hash");
                if (!passHash.equals(password)) return null;

                return new UserSession(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        Enums.Role.valueOf(rs.getString("role")),
                        (Integer) rs.getObject("branch_id")
                );
            }
        }
    }
}
