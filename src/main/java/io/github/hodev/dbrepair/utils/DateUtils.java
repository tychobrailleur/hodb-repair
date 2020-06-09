package io.github.hodev.dbrepair.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public final class DateUtils {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    private DateUtils() {}

    public static String formatTimestamp(Timestamp timestamp) {
        return formatter.format(timestamp);
    }
}
