package io.github.hodev.dbrepair;

import io.github.hodev.dbrepair.types.Type;
import io.github.hodev.dbrepair.types.TypeFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HsqlDatabaseConnection extends DatabaseConnection {
    private final static String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    private final static String HSQLDB_URL_PREFIX = "jdbc:hsqldb:file:";

    private String type; // TODO make an enum
    private String dbName;
    private String user;
    private String password; // Beware!  Password is held in memory!


    private Connection connection;

    public HsqlDatabaseConnection(String type, String dbName, String user, String password) {
        try {
            Class.forName(JDBC_DRIVER);
            this.dbName = dbName;
            this.type = type;
            this.user = user;
            this.password = password;
            initConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Init creates the parent directories for the output database if it doesn't exist,
     * and cleans up the files that exist already.
     */
    @Override
    public void init() {
        String outputDbLocation = dbName;
        File outputDb = new File(outputDbLocation + ".properties");
        if (outputDb.exists()) {
            String[] dbFiles = new String[] { ".data", ".script", ".tmp", ".log", ".properties", ".backup" };
            for (String dbFile: dbFiles) {
                File dbFileFile = new File(outputDbLocation + dbFile);
                dbFileFile.delete();
            }
        } else if (!outputDb.getParentFile().exists()) {
            outputDb.getParentFile().mkdirs();
        }
    }

    @Override
    public List<String> listTables() {
        List<String> tables = new ArrayList<>();

        Statement statement = null;
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            final ResultSet resultSet = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'");
            if (resultSet != null) {
                while (resultSet.next()) {
                    tables.add(resultSet.getString(1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }

        return tables;
    }

    @Override
    public Map<String, String> listColumns(String tableName) {
        final Map<String, String> columnTypes = new HashMap<>();
        Statement statement = null;
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            final ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s LIMIT 1", tableName));
            if (resultSet != null) {

                final ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    columnTypes.put(
                            metaData.getColumnName(i),
                            metaData.getColumnTypeName(i)
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            closeStatement(statement);
        }

        return columnTypes;
    }

    public List<Map<String, Type>> listRows(DbTable table) {
        Map<String, String> columnTypes = table.getColumns();
        List<Map<String, Type>> entries = new ArrayList<>();

        Statement statement = null;
        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            final ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s", table.getName()));
            if (resultSet != null) {
                while (resultSet.next()) {
                    final Map<String, Type> row = new HashMap<>();
                    columnTypes.forEach((key, value) -> {
                        try {
                            Type val = switch (value) {
                                case "VARCHAR" -> TypeFactory.createType(resultSet.getString(key));
                                case "BOOLEAN" -> TypeFactory.createType(resultSet.getBoolean(key));
                                case "INTEGER", "TINYINT", "SMALLINT" -> TypeFactory.createType(resultSet.getInt(key));
                                case "DOUBLE" -> TypeFactory.createType(resultSet.getDouble(key));
                                case "TIMESTAMP" -> TypeFactory.createType(resultSet.getTimestamp(key));
                                default -> throw new RuntimeException("Unknown Column type: " + value);
                            };

                            row.put(key, val);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    entries.add(row);
                }
            }
        } catch (Exception e) {
            // TODO Handle exception.
            e.printStackTrace();
        }  finally {
            closeStatement(statement);
        }

        return entries;
    }

    @Override
    public boolean execute(String insert) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return statement.execute(insert);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
        }

        return false;
    }

    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection == null;
    }

    private void initConnection() {
        try {
            close();

            // TODO For now assuming file.  Use type.
            this.connection = DriverManager.getConnection(HSQLDB_URL_PREFIX + dbName, user, password);
        } catch (Exception e) {
            // TODO handle exception.
            e.printStackTrace();
        }
    }
}
