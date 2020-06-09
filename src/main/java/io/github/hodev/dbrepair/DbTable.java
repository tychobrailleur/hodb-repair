package io.github.hodev.dbrepair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbTable {

    private String name;
    private List<String> columns;

    private List<Map<String, Object>> rows;

    public DbTable(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public DbTable columns(List<String> columns) {
        this.columns.addAll(columns);
        return this;
    }

    public DbTable addRow(Map<String, Object> row) {
        this.rows.add(row);
        return this;
    }

    public DbTable build() {
        return this;
    }
}
