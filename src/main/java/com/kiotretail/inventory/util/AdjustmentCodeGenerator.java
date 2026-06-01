package com.kiotretail.inventory.util;

import com.kiotretail.inventory.dao.StockAdjustmentDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Generates human-friendly stock adjustment codes in the format
 * {@code SA-yyyyMMdd-NNN} where NNN is a zero-padded daily sequence number.
 *
 * <p>Sequence resets every day. The DAO is queried for the highest sequence
 * already used today; the next number is one above that.</p>
 */
public final class AdjustmentCodeGenerator {

    private static final String PREFIX = "SA";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String SEQUENCE_FORMAT = "%03d";

    private AdjustmentCodeGenerator() {
    }

    /**
     * Generates the next available code for today's date.
     *
     * @param dao DAO used to look up the current max sequence; must not be null.
     * @return a code such as "SA-20260601-001"
     */
    public static String generate(StockAdjustmentDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException("StockAdjustmentDAO must not be null");
        }
        String today = LocalDate.now().format(DATE_FORMAT);
        int maxSeq = dao.getMaxSequenceForDate(today);
        int nextSeq = maxSeq + 1;
        return PREFIX + "-" + today + "-" + String.format(SEQUENCE_FORMAT, nextSeq);
    }
}
