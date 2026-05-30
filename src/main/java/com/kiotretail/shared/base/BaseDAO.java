package com.kiotretail.shared.base;

import com.kiotretail.shared.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class for all DAO implementations.
 * Provides shared connection acquisition and resource cleanup helpers.
 */
public abstract class BaseDAO {

    /**
     * Acquires a database connection from the shared {@link DatabaseUtil}.
     *
     * @return an open JDBC {@link Connection}
     * @throws SQLException if a connection cannot be obtained
     */
    protected Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection();
    }

    /**
     * Closes any number of {@link AutoCloseable} resources, swallowing exceptions.
     * Null entries are ignored. Useful in {@code finally} blocks.
     *
     * @param resources resources to close in order
     */
    protected void closeQuietly(AutoCloseable... resources) {
        if (resources == null) {
            return;
        }
        for (AutoCloseable resource : resources) {
            if (resource == null) {
                continue;
            }
            try {
                resource.close();
            } catch (Exception ignored) {
                // Intentionally swallowed: cleanup must not mask the original error.
            }
        }
    }

    /**
     * Closes the given {@link Connection} without throwing.
     * Delegates to {@link #closeQuietly(AutoCloseable...)}.
     *
     * @param conn the connection to close (may be {@code null})
     */
    protected void closeConnection(Connection conn) {
        closeQuietly(conn);
    }
}
