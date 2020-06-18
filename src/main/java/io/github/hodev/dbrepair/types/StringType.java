package io.github.hodev.dbrepair.types;

import io.github.hodev.dbrepair.utils.StringUtils;

public class StringType implements Type {

    private final String value;

    public StringType(String value) {
        this.value = value;
    }

    public String valueAsSql() {
        return String.format("'%s'",
            StringUtils.escapeNewlines(
                StringUtils.escapeQuotes(value)
            ));
    }

    @Override
    public Object value() {
        return value;
    }
}
