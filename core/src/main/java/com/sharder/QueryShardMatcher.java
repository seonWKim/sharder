package com.sharder;

import java.util.List;

import com.sharder.shard.SharderDatabase;

public interface QueryShardMatcher {
    boolean match(String query, SharderDatabase database);
    List<SharderDatabase> match(String query, List<SharderDatabase> databases);
}
