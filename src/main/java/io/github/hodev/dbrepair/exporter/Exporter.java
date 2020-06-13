package io.github.hodev.dbrepair.exporter;

import io.github.hodev.dbrepair.DbTable;

import java.util.List;

public interface Exporter {

    public void exportTables(List<DbTable> tables);
}
