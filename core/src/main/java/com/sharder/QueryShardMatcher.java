package com.sharder;

import java.util.List;

import com.sharder.shard.SharderDatabase;

// TODO: Should we receive query as string? What about using an marker interface?
public interface QueryShardMatcher {
    boolean match(String query, SharderDatabase database);
    <T extends SharderDatabase> List<T> match(String query, List<T> databases);
}
