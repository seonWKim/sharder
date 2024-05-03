package com.sharder;

import java.util.ArrayList;
import java.util.List;

import com.sharder.query.SimpleQuery;
import com.sharder.shard.ShardDefinition;
import com.sharder.shard.SharderDatabase;

public class SimpleQueryShardMatcher implements QueryShardMatcher {
    @Override
    public boolean match(String query, SharderDatabase database) {
        if (database.shardDefinitions().isEmpty()) {
            return true;
        }

        SimpleQuery simpleQuery = SimpleQuery.of(query);
        if (simpleQuery.conditionExpression() == null) {
            return true;
        }

        return database.shardDefinitions().stream().allMatch(it -> it.match(simpleQuery.conditionExpression()));
    }

    @Override
    public <T extends SharderDatabase> List<T> match(String query, List<T> databases) {
        SimpleQuery simpleQuery = SimpleQuery.of(query);
        if (simpleQuery.conditionExpression() == null) {
            return databases;
        }

        final List<T> result = new ArrayList<>();
        for (T database : databases) {
            if (match(simpleQuery, database)) {
                result.add(database);
            }
        }

        return result;
    }

    private boolean match(SimpleQuery simpleQuery, SharderDatabase database) {
        if (database.shardDefinitions().isEmpty()) {
            return true;
        }

        // if there are no shard definitions for that specific table, we return true
        if (!database.shardDefinitionsByTableName().containsKey(simpleQuery.tableName())) {
            return true;
        }

        return database.shardDefinitionsByTableName().get(simpleQuery.tableName())
                       .stream().allMatch(it -> it.match(simpleQuery.conditionExpression()));
    }
}
