package com.kiotretail.shared.util;

/**
 * Generates unique codes for entities using millisecond timestamps.
 * Simple and unique enough for typical retail workloads (no DB lookup needed).
 */
public final class CodeGenerator {

    private CodeGenerator() {
        // Prevent instantiation
    }

    public static String generateProductCode() {
        return "SP" + System.currentTimeMillis();
    }

    public static String generateCustomerCode() {
        return "KH" + System.currentTimeMillis();
    }

    public static String generateEmployeeCode() {
        return "NV" + System.currentTimeMillis();
    }

    public static String generateInvoiceCode() {
        return "HD" + System.currentTimeMillis();
    }

    public static String generateOrderCode() {
        return "DH" + System.currentTimeMillis();
    }
}
