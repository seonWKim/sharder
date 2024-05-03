package com.sharder.shard;

import java.util.List;

public interface SharderDatabase {
    String databaseName();

    List<ShardDefinition> shardDefinitions();
}
