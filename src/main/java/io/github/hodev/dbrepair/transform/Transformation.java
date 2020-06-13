package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;

import java.util.List;

public interface Transformation {
    int getValidAfterVersion();
    void setValidAfterVersion(int version);

    void perform(List<DbTable> tables);
}
