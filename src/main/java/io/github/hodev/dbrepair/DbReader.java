package io.github.hodev.dbrepair;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

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

    public void writeNewDb(String outputDir, String fileLocation) {
        String connUrl = "jdbc:hsqldb:file:" + outputDir + "/database";
        String username = "sa";
        String password = "";

        try {

            // Load Schema
            final URL scriptResource = DbReader.class.getClassLoader().getResource("database-3.0.0.script");
            File script = Paths.get(scriptResource.toURI()).toFile();

            try (Connection conn = DriverManager.getConnection(connUrl, username, password)) {
                BufferedReader in = new BufferedReader(new FileReader(script));
                Statement statement = conn.createStatement();
                String line;
                while ((line = in.readLine()) != null) {
                    statement.execute(line);
                }
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }

            // Load data
            Collection<File> sqlFiles = FileUtils.listFiles(new File("/tmp"), new String[]{"sql"}, false);
            sqlFiles.forEach(file -> {
                System.out.println("Loading file " + file + "...");
                int lineNo = 0;
                try {
                    try (Connection conn = DriverManager.getConnection(connUrl, username, password)) {
                        Statement statement = conn.createStatement();

                        BufferedReader in = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = in.readLine()) != null) {
                            lineNo++;
                            statement.execute(line);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading table for file: " + file);
                    System.err.println("Line number: " + lineNo);
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
