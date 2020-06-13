package io.github.hodev.dbrepair;

import io.github.hodev.dbrepair.types.Type;

import java.util.List;
import java.util.Map;

public abstract class DatabaseConnection {
    public abstract void init();

    public abstract List<String> listTables();

    public abstract Map<String, String> listColumns(String tableName);

    public abstract List<Map<String, Type>> listRows(DbTable table);

    /**
     * Executes a SQL statement against this database connection, no returned result expected.
     * @param insert
     * @return
     */
    public abstract boolean execute(String insert);

    public abstract boolean close();
}
