package io.github.hodev.dbrepair;

import io.github.hodev.dbrepair.types.Type;

import java.util.List;
import java.util.Map;

public abstract class DatabaseConnection {

    public abstract List<String> listTables();

    public abstract Map<String, String> listColumns(String tableName);

    public abstract List<Map<String, Type>> listRows(DbTable table);

    public abstract boolean close();
}
