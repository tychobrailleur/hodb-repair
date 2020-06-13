package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;

import java.util.List;

public interface Transformation {

    void perform(List<DbTable> tables);
}
