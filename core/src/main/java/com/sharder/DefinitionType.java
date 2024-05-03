package com.sharder;

import lombok.Getter;

@Getter
public enum DefinitionType { // Is there a better databaseName for this?
    QUERY(1),
    SHARD(2);

    private final int mask;

    DefinitionType(int mask) {
        this.mask = mask;
    }
}
