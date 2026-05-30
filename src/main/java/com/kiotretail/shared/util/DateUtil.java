package com.kiotretail.shared.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtil() {
    }

    public static String format(LocalDateTime dt) {
        return DISPLAY_FORMAT.format(dt);
    }

    public static String formatDate(LocalDate d) {
        return DATE_ONLY.format(d);
    }

    public static LocalDateTime parseDateTime(String text) {
        return LocalDateTime.parse(text, DISPLAY_FORMAT);
    }

    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DATE_ONLY);
    }
}
