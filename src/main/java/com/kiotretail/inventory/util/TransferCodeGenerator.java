package com.kiotretail.inventory.util;

import com.kiotretail.inventory.dao.StockTransferDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Generates stock transfer codes in {@code ST-yyyyMMdd-NNN} format.
 *
 * <p>Sequence resets daily; the DAO provides the highest sequence used
 * today and the next number is one above that.</p>
 */
public final class TransferCodeGenerator {

    private static final String PREFIX = "ST";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String SEQUENCE_FORMAT = "%03d";

    private TransferCodeGenerator() {}

    public static String generate(StockTransferDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException("StockTransferDAO must not be null");
        }
        String today = LocalDate.now().format(DATE_FORMAT);
        int maxSeq = dao.getMaxSequenceForDate(today);
        int nextSeq = maxSeq + 1;
        return PREFIX + "-" + today + "-" + String.format(SEQUENCE_FORMAT, nextSeq);
    }
}
