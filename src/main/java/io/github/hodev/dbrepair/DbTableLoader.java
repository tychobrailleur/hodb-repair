package io.github.hodev.dbrepair;

import java.util.List;
import java.util.Optional;

public class DbTableLoader {

    public Optional<DbTable> loadTable(DatabaseConnection connection, String tableName, List<String> failures) {
        Optional<DbTable> opt = Optional.empty();

        // Not available before HSQLDB 2.5.0
        // statement.execute(String.format("PERFORM EXPORT SCRIPT FOR TABLE %s DATA TO '%s'", table, "/tmp/" + table + ".sql"));

        try {
            DbTable dbTable = new DbTable(tableName.toUpperCase());
            dbTable.columns(connection.listColumns(tableName));
            dbTable.rows(connection.listRows(dbTable));
            return Optional.of(dbTable);
        } catch (Exception e) {
            failures.add(tableName);
        }

        return opt;
    }
}
