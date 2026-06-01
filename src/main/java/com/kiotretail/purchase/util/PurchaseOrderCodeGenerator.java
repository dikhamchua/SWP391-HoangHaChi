package com.kiotretail.purchase.util;

import com.kiotretail.purchase.dao.PurchaseOrderDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Generates human-friendly purchase order codes in the format
 * {@code PO-yyyyMMdd-NNN} where NNN is a zero-padded daily sequence number.
 *
 * <p>Sequence resets every day. The DAO is queried for the highest sequence
 * already used today; the next number is one above that.</p>
 */
public final class PurchaseOrderCodeGenerator {

    private static final String PREFIX = "PO";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String SEQUENCE_FORMAT = "%03d";

    private PurchaseOrderCodeGenerator() {
    }

    /**
     * Generates the next available code for today's date.
     *
     * @param dao DAO used to look up the current max sequence; must not be null.
     * @return a code such as "PO-20260601-001"
     */
    public static String generate(PurchaseOrderDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException("PurchaseOrderDAO must not be null");
        }
        String today = LocalDate.now().format(DATE_FORMAT);
        int maxSeq = dao.getMaxSequenceForDate(today);
        int nextSeq = maxSeq + 1;
        return PREFIX + "-" + today + "-" + String.format(SEQUENCE_FORMAT, nextSeq);
    }
}
