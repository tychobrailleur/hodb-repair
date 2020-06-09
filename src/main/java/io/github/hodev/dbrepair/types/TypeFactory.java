package io.github.hodev.dbrepair.types;

import java.sql.Timestamp;

public class TypeFactory {
    private final static NullType NULL_TYPE = new NullType();

    public static Type createType(Object val) {
        if (val == null) {
            return NULL_TYPE;
        } else if (val instanceof String) {
            return new StringType((String)val);
        } else if (val instanceof Integer) {
            return new IntegerType((Integer)val);
        } else if (val instanceof Boolean) {
            return new BooleanType((Boolean)val);
        } else if (val instanceof Double) {
            return new DoubleType((Double)val);
        } else if (val instanceof Timestamp) {
            return new TimestampType((Timestamp)val);
        } else {
            throw new RuntimeException("Unknown type for val: " + val);
        }
    }
}
