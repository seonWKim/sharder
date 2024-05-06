package io.github.seonwkim;

import java.util.ArrayList;
import java.util.List;

import io.github.seonwkim.shard.SharderDatabase;

/**
 * Simple query shard matcher.<br>
 *
 * When a single table has multiple shard definitions, it will work as an OR operation. For example, when the shard definitions are as follows: <br>
 * <pre>
 * 1) members.id > 10
 * 2) members.id < 20
 * </pre>
 * Above will match databases that have either {@code member.id > 10} OR {@code member.id < 20}. <br>
 */
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

        return database.shardDefinitionsByTableName().get(simpleQuery.tableName())
                       .stream().anyMatch(it -> it.match(simpleQuery.conditionExpression()));
    }
}
