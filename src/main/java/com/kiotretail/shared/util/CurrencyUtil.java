package com.kiotretail.shared.util;

import java.text.DecimalFormat;

/**
 * Utility class for formatting and parsing Vietnamese Dong (VND) currency values.
 */
public final class CurrencyUtil {

    private static final DecimalFormat VND_FORMAT = new DecimalFormat("#,###");

    private CurrencyUtil() {
        // Prevent instantiation
    }

    /**
     * Formats an amount as a VND currency string without the currency symbol.
     *
     * @param amount the amount in VND
     * @return formatted string (e.g. "1,500,000")
     */
    public static String formatVND(long amount) {
        return VND_FORMAT.format(amount);
    }

    /**
     * Formats an amount as a VND currency string with the dong symbol.
     *
     * @param amount the amount in VND
     * @return formatted string with symbol (e.g. "1,500,000 ₫")
     */
    public static String formatVNDWithSymbol(long amount) {
        return formatVND(amount) + " ₫";
    }

    /**
     * Parses a currency-formatted string into a long value by stripping non-digit characters.
     *
     * @param text the input text (may contain separators or the currency symbol)
     * @return parsed amount as long, or 0 if input is null/empty/has no digits
     */
    public static long parseCurrency(String text) {
        if (text == null) {
            return 0L;
        }
        String digits = text.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(digits);
    }
}
