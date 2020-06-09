package io.github.hodev.dbrepair.types;

public class DoubleType implements Type {

    private final double value;

    public DoubleType(double value) {
        this.value = value;
    }

    @Override
    public String valueAsSql() {
        return String.valueOf(value);
    }
}
