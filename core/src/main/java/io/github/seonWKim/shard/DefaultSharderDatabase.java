package io.github.seonWKim.shard;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultSharderDatabase implements SharderDatabase {
    private final String databaseName;
    private final List<ShardDefinition> shardDefinitions;

    private final Map<String, List<ShardDefinition>> shardDefinitionsByTableName;

    public DefaultSharderDatabase(String databaseName, List<ShardDefinition> shardDefinitions) {
        this.databaseName = databaseName;
        this.shardDefinitions = shardDefinitions;
        this.shardDefinitionsByTableName =
                shardDefinitions.stream().collect(Collectors.groupingBy(ShardDefinition::tableName));
    }

    @Override
    public String databaseName() {
        return databaseName;
    }

    @Override
    public List<ShardDefinition> shardDefinitions() {
        return shardDefinitions;
    }

    @Override
    public Map<String, List<ShardDefinition>> shardDefinitionsByTableName() {
        return shardDefinitionsByTableName;
    }
}
