package io.github.hodev.dbrepair.importer;

import io.github.hodev.dbrepair.DatabaseConnection;
import io.github.hodev.dbrepair.DbReader;
import io.github.hodev.dbrepair.RepairConfig;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Imports a list of SQL files into a database.
 */
public class DbSqlFileImport implements Importer {

    private final static String[] SQL_FILES_EXT = new String[]{ "sql" };
    private RepairConfig config;

    public DbSqlFileImport(RepairConfig config) {
        this.config = config;
    }

    @Override
    public void importData(DatabaseConnection connection) {
        cleanupDatabase(connection);
        loadSchema(connection);
        loadFiles(connection);
    }

    private void cleanupDatabase(DatabaseConnection connection) {
        connection.init();
    }

    private void loadFiles(DatabaseConnection connection) {
        try {
            Collection<File> sqlFiles = FileUtils.listFiles(
                    new File(config.getTempDirectory()),
                    SQL_FILES_EXT,
                    false
            );
            sqlFiles.forEach(file -> importSqlFile(file, connection));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importSqlFile(File file, DatabaseConnection connection) {
        System.out.println("Loading file " + file + "...");
        int lineNo = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                lineNo++;
                connection.execute(line);
            }
        } catch (Exception e) {
            System.err.println("Error loading table for file: " + file);
            System.err.println("Line number: " + lineNo);
            e.printStackTrace();
        }
    }

    private void loadSchema(DatabaseConnection connection) {
        try {

            InputStream scriptResource = null;

            if ("current".equalsIgnoreCase(config.getTargetDbVersion())) {
                final File dbDirectory = new File(config.getInputDbLocation());
                final File file = new File(dbDirectory.getParent(), "database.script");
                scriptResource = new FileInputStream(file);
            } else {
                scriptResource = DbReader.class.getResourceAsStream(
                    String.format("/database-%s.script", config.getTargetDbVersion())
                );
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(scriptResource, StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                // Only apply the `CREATE` commands.
                if (line.startsWith("CREATE")) {
                    connection.execute(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
