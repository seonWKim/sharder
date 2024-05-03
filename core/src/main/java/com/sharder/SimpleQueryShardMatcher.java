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
        if (!simpleQuery.hasWhereStatement()) {
            return true;
        }

        return database.shardDefinitions().stream().anyMatch(it -> it.match(simpleQuery.condition()));
    }

    @Override
    public List<SharderDatabase> match(String query, List<SharderDatabase> databases) {
        SimpleQuery simpleQuery = SimpleQuery.of(query);
        if (!simpleQuery.hasWhereStatement()) {
            return databases;
        }

        final List<SharderDatabase> result = new ArrayList<>();
        for (SharderDatabase database : databases) {
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
        return database.shardDefinitions().stream().anyMatch(it -> it.match(simpleQuery.condition()));
    }
}
