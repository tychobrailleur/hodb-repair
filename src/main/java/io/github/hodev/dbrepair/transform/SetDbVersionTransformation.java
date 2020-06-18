package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;
import io.github.hodev.dbrepair.types.Type;
import io.github.hodev.dbrepair.types.TypeFactory;
import io.github.hodev.dbrepair.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SetDbVersionTransformation implements Transformation {
    private String version;
    private int validAfterVersion;

    public SetDbVersionTransformation(String version) {
        this.version = version;
    }

    @Override
    public int getValidAfterVersion() {
        return validAfterVersion;
    }

    public void setValidAfterVersion(int validAfterVersion) {
        this.validAfterVersion = validAfterVersion;
    }


    @Override
    public void perform(List<DbTable> tables) {

        for (DbTable table: tables) {
            if (table.getName().equalsIgnoreCase("USERCONFIGURATION")) {
                AtomicBoolean foundVersion = new AtomicBoolean(false);
                final List<Map<String, Type>> rows = table.getRows();
                final List<Map<String, Type>> transformedRows = new ArrayList<>();

                rows.forEach(stringTypeMap -> {
                    if (stringTypeMap.get("CONFIG_KEY").value().toString().equals("DBVersion")) {
                        foundVersion.set(true);
                        final Map<String, Type> dbVersionRow = new HashMap<>();
                        dbVersionRow.put("CONFIG_KEY", TypeFactory.createType("DBVersion"));
                        int val = (int)stringTypeMap.get("CONFIG_VALUE").value();
                        System.out.println("DBVersion = " + val);

                        if (StringUtils.versionToInt(version) < val) {
                            dbVersionRow.put("CONFIG_VALUE", TypeFactory.createType(StringUtils.versionToInt(version)));
                        } else {
                            dbVersionRow.put("CONFIG_VALUE", TypeFactory.createType(val));
                        }

                        transformedRows.add(dbVersionRow);
                    } else {
                        transformedRows.add(stringTypeMap);
                    }
                });

                if (!foundVersion.get()) {
                    System.out.println("No DBVersion found, adding: " + version);

                    final Map<String, Type> dbVersionRow = new HashMap<>();
                    dbVersionRow.put("CONFIG_KEY", TypeFactory.createType("DBVersion"));
                    dbVersionRow.put("CONFIG_VALUE", TypeFactory.createType(StringUtils.versionToInt(version)));

                    transformedRows.add(dbVersionRow);
                }

                table.rows(transformedRows);
            }
        }
    }
}
