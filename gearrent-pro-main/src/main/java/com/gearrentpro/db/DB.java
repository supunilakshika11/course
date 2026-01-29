package com.gearrentpro.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DB {

    private static final String URL =
        "jdbc:mysql://localhost:8889/gear_rent_pro?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo";

    private static final String USER = "root";
    private static final String PASS = "root"; // MAMP default

    private DB(){}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
