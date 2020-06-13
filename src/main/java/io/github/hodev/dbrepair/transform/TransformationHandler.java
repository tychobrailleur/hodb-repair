package io.github.hodev.dbrepair.transform;

import io.github.hodev.dbrepair.DbTable;
import io.github.hodev.dbrepair.RepairConfig;
import io.github.hodev.dbrepair.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TransformationHandler {

    List<Transformation> transformationList = new ArrayList<>();

    public void add(Transformation transformation) {
        transformationList.add(transformation);
    }

    public void perform(RepairConfig config, List<DbTable> tables) {
        transformationList.forEach(transformation -> {
            if (StringUtils.versionToInt(config.getTargetDbVersion()) >= transformation.getValidAfterVersion()) {
                transformation.perform(tables);
            }
        });
    }
}
