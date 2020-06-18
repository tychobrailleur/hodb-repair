package io.github.hodev.dbrepair.types;

import io.github.hodev.dbrepair.utils.DateUtils;

import java.sql.Timestamp;

public class TimestampType implements Type {

    private final Timestamp value;

    public TimestampType(Timestamp value) {
        this.value = value;
    }

    public String valueAsSql() {
        return String.format("'%s'", DateUtils.formatTimestamp(value));
    }

    @Override
    public Object value() {
        return value;
    }
}
