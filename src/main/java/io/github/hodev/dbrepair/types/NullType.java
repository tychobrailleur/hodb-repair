package io.github.hodev.dbrepair.types;

public class NullType implements Type {

    @Override
    public String valueAsSql() {
        return "NULL";
    }

    @Override
    public Object value() {
        return null;
    }
}
