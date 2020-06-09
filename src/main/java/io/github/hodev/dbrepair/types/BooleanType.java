package io.github.hodev.dbrepair.types;

public class BooleanType implements Type {

    private final boolean value;

    public BooleanType(boolean value) {
        this.value = value;
    }

    @Override
    public String valueAsSql() {
        return value ? "TRUE" : "FALSE";
    }
}
