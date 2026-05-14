package com.kiotretail.util;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing SQL Server connection...");

        try {
            if (DatabaseUtil.testConnection()) {
                System.out.println("✅ Connection successful!");
            } else {
                System.out.println("❌ Connection failed!");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
