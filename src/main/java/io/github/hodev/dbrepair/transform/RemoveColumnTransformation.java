package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;
import io.github.hodev.dbrepair.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveColumnTransformation implements Transformation {

    private final String tableName;
    private final String[] columnNames;
    private int validAfterVersion = 300;

    public RemoveColumnTransformation(String tableName, String... columnNames) {
        this.tableName = tableName;
        this.columnNames = columnNames;
    }

    @Override
    public int getValidAfterVersion() {
        return validAfterVersion;
    }

    @Override
    public void setValidAfterVersion(int validAfterVersion) {
        this.validAfterVersion = validAfterVersion;
    }

    @Override
    public void perform(List<DbTable> tables) {

        for (DbTable table: tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                for (String columnName: columnNames) {
                    final List<Map<String, Type>> rows = table.getRows();
                    final List<Map<String, Type>> transformedRows = new ArrayList<>();
                    rows.forEach(stringTypeMap -> {
                        if (stringTypeMap.containsKey(columnName.toUpperCase())) {
                            stringTypeMap.remove(columnName.toUpperCase());
                        }
                        transformedRows.add(stringTypeMap);
                    });

                    Map<String, String> columns = new HashMap<>(table.getColumns());
                    columns.remove(columnName.toUpperCase());

                    table.columns(columns);
                    table.rows(transformedRows);
                }
            }
        }
    }
}
