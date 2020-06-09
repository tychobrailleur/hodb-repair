package io.github.hodev.dbrepair.types;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanTypeTest {

    @Test
    public void testTrueValue() {
        assertEquals("TRUE", new BooleanType(true).valueAsSql());
    }

    @Test
    public void testFalseValue() {
        assertEquals("FALSE", new BooleanType(false).valueAsSql());
    }
}