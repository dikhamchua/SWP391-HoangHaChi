package com.kiotretail.shared.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection Utility - MySQL.
 *
 * Credentials read from environment variables with safe local-dev defaults.
 * Override via DB_URL / DB_USER / DB_PASSWORD when deploying. See HRS-001.
 */
public class DatabaseUtil {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String DEFAULT_URL =
        "jdbc:mysql://localhost:3306/DBFinora"
        + "?useUnicode=true"
        + "&characterEncoding=UTF-8"
        + "&characterSetResults=UTF-8"
        + "&connectionCollation=utf8mb4_unicode_ci"
        + "&serverTimezone=Asia/Ho_Chi_Minh"
        + "&useSSL=false"
        + "&allowPublicKeyRetrieval=true";

    private static final String JDBC_URL  = envOr("DB_URL", DEFAULT_URL);
    private static final String DB_USER   = envOr("DB_USER", "root");
    private static final String DB_SECRET = envOr("DB_PASSWORD", "root");

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    private static String envOr(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? fallback : v;
    }

    /** Tao connection moi moi lan goi (khong dung pool). */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_SECRET);
        // Force utf8mb4 cho session: MySQL server my.cnf default latin1, URL param
        // characterEncoding khong tu dong gan SET NAMES nen tieng Viet bi mojibake.
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.execute("SET character_set_client = utf8mb4");
            stmt.execute("SET character_set_connection = utf8mb4");
            stmt.execute("SET character_set_results = utf8mb4");
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Quick smoke test: java -cp ... com.kiotretail.shared.util.DatabaseUtil */
    public static void main(String[] args) throws SQLException {
        try (Connection c = getConnection()) {
            System.out.println("Connected: " + c);
        }
    }
}
