package com.kiotretail.shared.base;

import com.kiotretail.shared.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * BaseService provides transaction management helpers for service-layer classes.
 * Subclasses use these methods to coordinate multi-step DAO operations under a
 * single JDBC transaction.
 */
public class BaseService {

    /**
     * Open a new connection and switch it to manual-commit mode.
     */
    protected Connection beginTransaction() throws SQLException {
        Connection conn = DatabaseUtil.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Commit the current transaction on the given connection.
     */
    protected void commitTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    /**
     * Roll back the current transaction silently. Suppresses any SQLException so
     * callers in error-handling paths are not masked by secondary failures.
     */
    protected void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // intentionally ignored
            }
        }
    }

    /**
     * Restore auto-commit mode and close the connection silently.
     */
    protected void closeTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
                // intentionally ignored
            }
            try {
                conn.close();
            } catch (SQLException ignored) {
                // intentionally ignored
            }
        }
    }
}
