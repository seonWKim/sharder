package com.sharder.shard;

import java.util.List;

public record SharderDatabase(
        String databaseName,
        List<ShardDefinition> shardDefinitions
) {}
