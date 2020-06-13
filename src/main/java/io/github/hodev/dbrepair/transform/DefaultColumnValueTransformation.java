package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;
import io.github.hodev.dbrepair.types.Type;
import io.github.hodev.dbrepair.types.TypeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Replaces a potentially null value in column <code>columnName</code> in the table
 * <code>tableName</code> with default value <code>defaultValue</code>.
 */
public class DefaultColumnValueTransformation implements Transformation {

    private final String tableName;
    private final String columnName;
    private final String columnType; // Type of the column if we need to create it.
    private final Type defaultValue;
    private int validAfterVersion = 300;

    public DefaultColumnValueTransformation(String tableName, String columnName, String columnType, Object defaultValue) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = columnType;
        this.defaultValue = TypeFactory.createType(defaultValue);
    }

    public void setValidAfterVersion(int validAfterVersion) {
        this.validAfterVersion = validAfterVersion;
    }

    @Override
    public int getValidAfterVersion() {
        return validAfterVersion;
    }

    @Override
    public void perform(List<DbTable> tables) {

        for (DbTable table: tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                final List<Map<String, Type>> rows = table.getRows();
                final List<Map<String, Type>> transformedRows = new ArrayList<>();
                rows.forEach(stringTypeMap -> {
                    stringTypeMap.computeIfAbsent(columnName.toUpperCase(), k -> defaultValue);
                    transformedRows.add(stringTypeMap);
                });

                if (!table.getColumns().containsKey(columnName.toUpperCase())) {
                    Map<String, String> columns = new HashMap<>(table.getColumns());
                    columns.put(columnName, columnType);

                    table.columns(columns);
                }

                table.rows(transformedRows);
            }
        }
    }
}
