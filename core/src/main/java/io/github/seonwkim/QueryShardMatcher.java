package io.github.seonwkim;

import java.util.List;

import io.github.seonwkim.shard.SharderDatabase;

/**
 * Interface for matching queries to databases.
 */
public interface QueryShardMatcher {
    boolean match(String query, SharderDatabase database);
    <T extends SharderDatabase> List<T> match(String query, List<T> databases);
}
