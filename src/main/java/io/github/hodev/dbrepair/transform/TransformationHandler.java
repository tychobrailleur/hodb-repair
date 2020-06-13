package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;

import java.util.ArrayList;
import java.util.List;

public class TransformationHandler {

    List<Transformation> transformationList = new ArrayList<>();

    public void add(Transformation transformation) {
        transformationList.add(transformation);
    }

    public void perform(List<DbTable> tables) {
        transformationList.forEach(transformation -> {
            transformation.perform(tables);
        });
    }
}
