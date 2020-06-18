package io.github.hodev.dbrepair.types;

public class IntegerType implements Type {

    private final int value;

    public IntegerType(int value) {
        this.value = value;
    }

    public String valueAsSql() {
        return String.valueOf(value);
    }

    @Override
    public Object value() {
        return value;
    }
}
