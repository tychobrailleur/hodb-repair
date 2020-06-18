package io.github.hodev.dbrepair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbReader {

    private final static String PASSWORD = "";
    private final static String USER = "sa";

    private final List<String> failures = new ArrayList<>();

    public List<DbTable> readAllTables(String database) {

        DatabaseConnection connection = new HsqlDatabaseConnection("file", database, USER, PASSWORD);
        List<String> tables = connection.listTables();

        final DbTableLoader loader = new DbTableLoader();
        final List<DbTable> tableList = new ArrayList<>();
        tables.forEach(table -> {
            Optional<DbTable> dbTable = loader.loadTable(connection, table, failures);
            dbTable.ifPresent(value -> tableList.add(dbTable.get()));
        });

        connection.close();

        return tableList;
    }
}
