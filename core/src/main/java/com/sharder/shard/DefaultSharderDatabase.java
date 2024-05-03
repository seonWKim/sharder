package com.sharder.shard;

import java.util.List;

public record DefaultSharderDatabase(String databaseName, List<ShardDefinition> shardDefinitions)
        implements SharderDatabase {}
