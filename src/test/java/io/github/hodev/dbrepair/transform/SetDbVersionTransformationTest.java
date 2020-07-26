package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;
import io.github.hodev.dbrepair.RepairConfig;
import io.github.hodev.dbrepair.types.IntegerType;
import io.github.hodev.dbrepair.types.StringType;
import io.github.hodev.dbrepair.types.Type;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SetDbVersionTransformationTest {

    @Test
    public void testAddMissingVersion() {
        DbTable spielerTable = new DbTable("SPIELER");
        DbTable userConfigTable = new DbTable("USERCONFIGURATION");

        List<DbTable> tables = List.of(spielerTable, userConfigTable);

        SetDbVersionTransformation transformation = new SetDbVersionTransformation("4.0.0");
        transformation.perform(tables);

        assertEquals(2, tables.size());
        DbTable tmp = tables.get(1);

        final List<Map<String, Type>> rows = tmp.getRows();
        assertEquals(1, rows.size());

        Map<String, Type> firstRow = rows.get(0);
        assertEquals(400, (int)firstRow.get("CONFIG_VALUE").value());
    }

    @Test
    public void testExistingVersionUnchangedVersion() {
        DbTable spielerTable = new DbTable("SPIELER");
        DbTable userConfigTable = new DbTable("USERCONFIGURATION");

        Map<String, Type> versionRow = new HashMap<>();
        versionRow.put("CONFIG_KEY", new StringType("DBVersion"));
        versionRow.put("CONFIG_VALUE", new IntegerType(42));
        userConfigTable.addRow(versionRow);

        List<DbTable> tables = List.of(spielerTable, userConfigTable);

        SetDbVersionTransformation transformation = new SetDbVersionTransformation("4.0.0");
        transformation.perform(tables);

        assertEquals(2, tables.size());
        DbTable tmp = tables.get(1);

        final List<Map<String, Type>> rows = tmp.getRows();
        assertEquals(1, rows.size());

        Map<String, Type> firstRow = rows.get(0);
        assertEquals(42, (int)firstRow.get("CONFIG_VALUE").value());
    }

    @Test
    public void testCurrentDoesntReplaceVersion() {
        DbTable spielerTable = new DbTable("SPIELER");
        DbTable userConfigTable = new DbTable("USERCONFIGURATION");

        Map<String, Type> versionRow = new HashMap<>();
        versionRow.put("CONFIG_KEY", new StringType("DBVersion"));
        versionRow.put("CONFIG_VALUE", new IntegerType(42));
        userConfigTable.addRow(versionRow);

        List<DbTable> tables = List.of(spielerTable, userConfigTable);

        SetDbVersionTransformation transformation = new SetDbVersionTransformation(RepairConfig.CURRENT_VERSION);
        transformation.perform(tables);

        assertEquals(2, tables.size());
        DbTable tmp = tables.get(1);

        final List<Map<String, Type>> rows = tmp.getRows();
        assertEquals(1, rows.size());

        Map<String, Type> firstRow = rows.get(0);
        assertEquals(42, (int)firstRow.get("CONFIG_VALUE").value());
    }
}
