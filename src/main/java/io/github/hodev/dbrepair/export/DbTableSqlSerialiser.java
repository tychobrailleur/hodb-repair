package io.github.hodev.dbrepair.export;

import io.github.hodev.dbrepair.DbTable;
import io.github.hodev.dbrepair.types.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DbTableSqlSerialiser {

    public void writeAsSql(String outputDir, DbTable dbTable) {

        BufferedWriter writer = null;
        try {
            File outputFile = new File(outputDir, dbTable.getName().toUpperCase() + ".sql");
            writer = new BufferedWriter(new FileWriter(outputFile));

            BufferedWriter finalWriter = writer;
            dbTable.getRows().forEach(row -> {
                try {
                    String statement = createInsertStatement(dbTable, row);
                    finalWriter.write(statement);
                    finalWriter.newLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String createInsertStatement(DbTable dbTable, Map<String, Type> row) {
        List<String> columnNames = new ArrayList<>(dbTable.getColumns().keySet());

        return "INSERT INTO " + dbTable.getName() + " (" +
                String.join(", ", columnNames) +
                ") VALUES (" +
                columnNames.stream()
                        .map(s -> row.get(s))
                        .map(this::serializeValue)
                        .collect(Collectors.joining(", "))
                + ")";
    }

    private String serializeValue(Type value) {
        return value.valueAsSql();
    }
}
