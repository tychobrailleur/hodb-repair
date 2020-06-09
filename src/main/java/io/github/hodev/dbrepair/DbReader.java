package io.github.hodev.dbrepair;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.FileUtils;
import org.hsqldb.Database;
import org.hsqldb.DatabaseType;
import org.hsqldb.cmdline.SqlFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class DbReader {

    private String driver = "org.hsqldb.jdbcDriver";
    private String name = "singleUser";
    private String pwd = "";
    private final static String HSQLDB_URL_PREFIX = "jdbc:hsqldb:file:";
    private String user = "sa";

    public void readDb(String database) {

        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(HSQLDB_URL_PREFIX + database, user, pwd);
            final Statement statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            final ResultSet resultSet = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'");

            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }

            tables.forEach(table -> {
                try {
                    // Not available before HSQLDB 2.5.0
                    // statement.execute(String.format("PERFORM EXPORT SCRIPT FOR TABLE %s DATA TO '%s'", table, "/tmp/" + table + ".sql"));


                    List<Map<String, Object>> entries = new ArrayList<>();
                    ResultSet entriesResultSet = statement.executeQuery(String.format("SELECT * FROM %s", table));
                    if (entriesResultSet != null) {
                        final Map<String, String> columnTypes =  new HashMap<>();
                        final ResultSetMetaData metaData = entriesResultSet.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            String columnType = metaData.getColumnTypeName(i);

                            columnTypes.put(columnName, columnType);
                        }

                        while (entriesResultSet.next()) {
                            final Map<String, Object> row = new HashMap<>();
                            columnTypes.entrySet().forEach(col -> {
                                try {
                                    Object val = switch (col.getValue()) {
                                        case "VARCHAR" -> entriesResultSet.getString(col.getKey());
                                        case "BOOLEAN" -> entriesResultSet.getBoolean(col.getKey());
                                        case "INTEGER", "TINYINT" -> entriesResultSet.getInt(col.getKey());
                                        case "DOUBLE" -> entriesResultSet.getDouble(col.getKey());
                                        case "TIMESTAMP" -> entriesResultSet.getTimestamp(col.getKey());
                                        default -> throw new RuntimeException("Unknown Column type: " + col.getValue());
                                    };

                                    if (val != null) {
                                        row.put(col.getKey(), val);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            entries.add(row);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    public void writeNewDb(String outputDir, String fileLocation) {
        String connUrl = "jdbc:hsqldb:file:" + outputDir + "/database";
        String username = "SA";
        String password = "";

        try {

            final URL resource = DbReader.class.getClassLoader().getResource("database-3.0.0.script");
            File script = Paths.get(resource.toURI()).toFile();

            FileUtils.copyFile(script, new File(outputDir, "database.script"));

//            Collection<File> sqlFiles = FileUtils.listFiles(new File("/tmp"), new String[] { "sql" }, false);
//            sqlFiles.forEach(file -> {
//                try {
//                    try (Connection conn = DriverManager.getConnection(connUrl, username, password)) {
//                        Statement statement = conn.createStatement();
//
//                        BufferedReader in  = new BufferedReader(new FileReader(file));
//                        String line;
//                        while ((line = in.readLine()) != null)   {
//                            statement.execute(line);
//                        }
//                    }
//                } catch (Exception throwables) {
//                    throwables.printStackTrace();
//                }
//            });

            try (Connection conn = DriverManager.getConnection(connUrl, username, password)) {
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM SPIELER");
                if (resultSet.next()) {
                    System.out.println(resultSet.getString(1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
