package io.github.hodev.dbrepair.utils;

public final class StringUtils {

    private StringUtils() {}

    public static String escapeQuotes(String value) {
        if (value != null) {
            return value.replaceAll("'", "''");
        }

        return value;
    }

    public static String escapeNewlines(String value) {
        if (value != null) {
            return value.replaceAll("\n", "\\n");
        }

        return value;
    }
}
