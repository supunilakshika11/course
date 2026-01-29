package com.gearrentpro.db;

import java.sql.Connection;
import java.sql.SQLException;

public final class Tx {
    private Tx(){}

    @FunctionalInterface
    public interface TxWork<T> {
        T run(Connection con) throws Exception;
    }

    public static <T> T inTx(TxWork<T> work) {
        try (Connection con = DB.getConnection()) {
            con.setAutoCommit(false);
            try {
                T out = work.run(con);
                con.commit();
                return out;
            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException("TX FAILED: " + e.getMessage(), e);
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB ERROR: " + e.getMessage(), e);
        }
    }
}
