/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.github.hodev.dbrepair;

import io.github.hodev.dbrepair.archiver.Archiver;
import io.github.hodev.dbrepair.exporter.DbTableSqlSerialiser;
import io.github.hodev.dbrepair.importer.DbSqlFileImport;
import io.github.hodev.dbrepair.importer.Importer;
import io.github.hodev.dbrepair.transform.*;
import org.apache.commons.cli.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * App is the main entry point of the repair tool.
 *
 * <p>The repair tool has two functions:</p>
 * <ul>
 *     <li>Read as much as possible data from the source database, and import into a new instance of the database;</li>
 *     <li>Export the database as a zip of SQL files.</li>
 * </ul>
 *
 * <p>In the case of a corrupt database, the tool may not be able to retrieve all the data.</p>
 */
public class App {

    private final static App APP = new App();

    public static void main(String[] args) {
        APP.runApp(args);
    }

    private void runApp(String[] args) {
        Logger.getLogger("hsqldb.db").setLevel(Level.WARNING);

        final Options options = new Options();
        options.addOption(Option
            .builder("d")
            .longOpt("db")
            .numberOfArgs(1)
            .required()
            .build());
        options.addOption(Option
            .builder("o")
            .longOpt("output-db")
            .numberOfArgs(1)
            .build());
        options.addOption(Option
            .builder("v")
            .longOpt("version")
            .numberOfArgs(1)
            .build());
        options.addOption(Option
            .builder("x")
            .longOpt("export")
            .numberOfArgs(1)
            .build());

        final CommandLineParser parser = new DefaultParser();

        try {
            final CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("o")) {
                repairDatabase(cmd);
            } else if (cmd.hasOption("x")) {
                dumpDatabase(cmd);
            }
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "Extract data from an existing db " +
                "and import into a new one after transformations.", options);
            System.exit(1);
        }
    }

    private void repairDatabase(CommandLine cmd) {
        String dbLocation = cmd.getOptionValue("d");
        String outputDbLocation = cmd.getOptionValue("o");

        final RepairConfig conf = new RepairConfig();
        conf.setInputDbLocation(dbLocation);
        conf.setOutputDbLocation(outputDbLocation);

        if (cmd.hasOption("v")) {
            conf.setTargetDbVersion(cmd.getOptionValue("v"));
        } else {
            // Use same database version as source.
            conf.setTargetDbVersion(RepairConfig.CURRENT_VERSION);
        }

        System.out.printf("Exporting db %s to new db at %s with version %s...%n",
            conf.getInputDbLocation(),
            conf.getOutputDbLocation(),
            conf.getTargetDbVersion()
        );

        List<DbTable> tables = readDatabase(conf);
        applyTransformations(conf, tables);
        writeSqlFileToDisk(conf, tables);
        loadSqlFileToNewDb(conf);
    }

    private void dumpDatabase(CommandLine cmd) {
        String exportLocation = cmd.getOptionValue("x");
        String dbLocation = cmd.getOptionValue("d");

        final RepairConfig conf = new RepairConfig();
        conf.setInputDbLocation(dbLocation);
        conf.setArchiveLocation(exportLocation);

        if (cmd.hasOption("v")) {
            conf.setTargetDbVersion(cmd.getOptionValue("v"));
        }

        List<DbTable> tables = readDatabase(conf);
        applyTransformations(conf, tables);
        writeSqlFileToDisk(conf, tables);

        Archiver archiver = new Archiver();
        String archivePath = archiver.perform(conf);

        System.out.printf("Database has been exported to %s.%n", archivePath);
    }

    private List<DbTable> readDatabase(RepairConfig config) {
        final DbReader dbReader = new DbReader();
        return dbReader.readAllTables(config.getInputDbLocation());
    }

    private void loadSqlFileToNewDb(RepairConfig config) {
        final DatabaseConnection connection = new HsqlDatabaseConnection(
            "file",
            config.getOutputDbLocation(),
            "sa",
            "");
        final Importer dbImporter = new DbSqlFileImport(config);
        dbImporter.importData(connection);
        connection.execute("SHUTDOWN");
    }

    private void writeSqlFileToDisk(RepairConfig config, List<DbTable> tables) {
        final DbTableSqlSerialiser serialiser = new DbTableSqlSerialiser();
        tables.forEach(dbTable -> serialiser.writeAsSql(config.getTempDirectory(), dbTable));
    }

    private void applyTransformations(RepairConfig config, List<DbTable> tables) {
        final TransformationHandler handler = new TransformationHandler();

        handler.add(new SetDbVersionTransformation(config.getTargetDbVersion()));
        if (!config.reuseSameVersion()) {
            final Transformation globalRankingDefault = new DefaultColumnValueTransformation(
                "VEREIN",
                "GLOBALRANKING",
                "INTEGER",
                0
            );
            globalRankingDefault.setValidAfterVersion(400);
            final Transformation hrfIdDefault = new DefaultColumnValueTransformation(
                "VEREIN",
                "HRF_ID",
                "INTEGER",
                32
            );
            final Transformation removePhysiologen = new RemoveColumnTransformation(
                "VEREIN",
                "PHYSIOLOGEN",
                "TWTRAINER"
            );
            removePhysiologen.setValidAfterVersion(400);

            handler.add(globalRankingDefault);
            handler.add(hrfIdDefault);
            handler.add(removePhysiologen);
        }

        handler.perform(config, tables);
    }
}
