package io.github.hodev.dbrepair.types;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringTypeTest {

    @Test
    public void testEmptyString() {
        StringType stringType = new StringType("");
        assertEquals("''", stringType.valueAsSql());
    }

    @Test
    public void testSimpleString() {
        StringType stringType = new StringType("hello");
        assertEquals("'hello'", stringType.valueAsSql());
    }

    @Test
    public void testStringWithQuote() {
        StringType stringType = new StringType("o'brien");
        assertEquals("'o''brien'", stringType.valueAsSql());
    }
}