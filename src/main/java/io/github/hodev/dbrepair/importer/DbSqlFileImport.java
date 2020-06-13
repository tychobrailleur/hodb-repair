package io.github.hodev.dbrepair.importer;

import io.github.hodev.dbrepair.DatabaseConnection;
import io.github.hodev.dbrepair.DbReader;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Imports a list of SQL files into a database.
 */
public class DbSqlFileImport implements Importer {

    private final static String[] SQL_FILES_EXT = new String[]{"sql"};
    private String inputFilesDir;

    public DbSqlFileImport(String inputFilesDir) {
        this.inputFilesDir = inputFilesDir;
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
                    new File(inputFilesDir),
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
            // TODO Make database input configurable.
            final URL scriptResource = DbReader.class.getClassLoader().getResource("database-3.0.0.script");
            File script = Paths.get(scriptResource.toURI()).toFile();

            BufferedReader in = new BufferedReader(new FileReader(script));
            String line;
            while ((line = in.readLine()) != null) {
                connection.execute(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
