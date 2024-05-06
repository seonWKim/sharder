package io.github.seonwkim.shard;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a database that is sharded.
 */
public interface SharderDatabase {

    /**
     * The name of the database.
     */
    String databaseName();

    /**
     * The shard definitions for the database.
     */
    default List<ShardDefinition> shardDefinitions() {
        return Collections.emptyList();
    }

    /**
     * The shard definitions grouped by table name. Used for quick lookup.
     */
    default Map<String, List<ShardDefinition>> shardDefinitionsByTableName() {
        return shardDefinitions().stream().collect(Collectors.groupingBy(ShardDefinition::tableName));
    }
}
