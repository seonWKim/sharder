package com.sharder.shard;

import java.util.List;
import java.util.Map;

public interface SharderDatabase {
    String databaseName();

    List<ShardDefinition> shardDefinitions();

    Map<String, List<ShardDefinition>> shardDefinitionsByTableName();
}
