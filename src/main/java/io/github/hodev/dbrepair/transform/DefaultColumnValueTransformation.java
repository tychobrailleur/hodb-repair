package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;
import io.github.hodev.dbrepair.types.Type;
import io.github.hodev.dbrepair.types.TypeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Replaces a potentially null value in column <code>columnName</code> in the table
 * <code>tableName</code> with default value <code>defaultValue</code>.
 */
public class DefaultColumnValueTransformation implements Transformation{

    private String tableName;
    private String columnName;
    private Type defaultValue;

    public DefaultColumnValueTransformation(String tableName, String columnName, Object defaultValue) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.defaultValue = TypeFactory.createType(defaultValue);
    }

    @Override
    public void perform(List<DbTable> tables) {

        for (DbTable table: tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                final List<Map<String, Type>> rows = table.getRows();
                final List<Map<String, Type>> transformedRows = new ArrayList<>();
                rows.forEach(stringTypeMap -> {
                    if (stringTypeMap.get(columnName.toUpperCase()) == null) {
                        stringTypeMap.put(columnName.toUpperCase(), defaultValue);
                    }

                    transformedRows.add(stringTypeMap);
                });

                table.rows(transformedRows);
            }
        }
    }
}
