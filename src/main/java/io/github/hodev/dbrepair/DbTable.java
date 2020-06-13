package io.github.hodev.dbrepair;

import io.github.hodev.dbrepair.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a database tables, with its columns and its rows.
 */
public class DbTable {

    /** Name of the table. */
    private final String name;

    /** Column type indexed by their name. */
    private final Map<String, String> columns;

    /** List of rows. */
    private final List<Map<String, Type>> rows;

    public String getName() {
        return name;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public List<Map<String, Type>> getRows() {
        return rows;
    }

    public DbTable(String name) {
        this.name = name;
        this.columns = new HashMap<>();
        this.rows = new ArrayList<>();
    }

    public DbTable columns(Map<String, String> columns) {
        this.columns.putAll(columns);
        return this;
    }

    public DbTable addRow(Map<String, Type> row) {
        this.rows.add(row);
        return this;
    }

    public DbTable rows(List<Map<String, Type>> rows) {
        this.rows.clear();
        this.rows.addAll(rows);
        return this;
    }

    public DbTable build() {
        return this;
    }
}
