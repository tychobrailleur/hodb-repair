package io.github.hodev.dbrepair.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testNullString() {
        assertNull(StringUtils.escapeQuotes(null));
    }
}