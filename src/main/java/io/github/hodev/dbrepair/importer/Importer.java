package io.github.hodev.dbrepair.importer;

import io.github.hodev.dbrepair.DatabaseConnection;

public interface Importer {

    /**
     * Imports data into a given {@link DatabaseConnection}.
     */
    void importData(DatabaseConnection connection);
}
