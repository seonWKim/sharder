package com.sharder;

import lombok.Getter;

@Getter
public enum DefinitionType {
    QUERY(1 << 1),
    SHARD(1 << 2);

    private final int mask;

    DefinitionType(int mask) {
        this.mask = mask;
    }

}
