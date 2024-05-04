package com.sharder;

import java.util.ArrayList;
import java.util.List;

import com.sharder.query.SimpleQuery;
import com.sharder.shard.SharderDatabase;

public class SimpleQueryShardMatcher implements QueryShardMatcher {
    @Override
    public boolean match(String query, SharderDatabase database) {
        if (database.shardDefinitions().isEmpty()) {
            return true;
        }

        SimpleQuery simpleQuery = SimpleQuery.of(query);
        return match(simpleQuery, database);
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
        if (simpleQuery.conditionExpression() == null ||
            database.shardDefinitions().isEmpty() ||
            !database.shardDefinitionsByTableName().containsKey(simpleQuery.tableName())) {
            return true;
        }

        // TODO:
        //  WHERE: id < 10 OR id >= 20
        //  SHARD: id >= 10 AND id < 20
        return database.shardDefinitionsByTableName().get(simpleQuery.tableName())
                       .stream().allMatch(it -> it.match(simpleQuery.conditionExpression()));
    }
}
