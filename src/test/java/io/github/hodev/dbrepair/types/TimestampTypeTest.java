package io.github.hodev.dbrepair.types;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class TimestampTypeTest {

    @Test
    public void testValueAsSql() {
        Timestamp timestamp = new Timestamp(1571907600000L);
        TimestampType timestampType = new TimestampType(timestamp);
        assertEquals("'2019-10-24 10:00:00.000'", timestampType.valueAsSql());
    }
}