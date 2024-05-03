package com.sharder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sharder.SimpleQueryService;
import com.sharder.SimpleQueryShardMatcher;
import com.sharder.config.record.SharderDatabaseHolder;
import com.sharder.config.record.SharderDatabases;
import com.sharder.query.SimpleQuery;

@Component
public class SimpleQueryServiceImpl implements SimpleQueryService {
    private final Map<String, SharderDatabaseHolder> databases;
    private final SimpleQueryShardMatcher shardMatcher = new SimpleQueryShardMatcher();
    public SimpleQueryServiceImpl(SharderDatabases databases) {this.databases = databases.configs();}

    @Override
    public List<Map<String, Object>> select(String query) {
        final List<Map<String, Object>> result = new ArrayList<>();

        for (var database : databases.values()) {
            if (shardMatcher.match(query, database.sharderDatabase())) {
                result.addAll(database.jdbcTemplate().queryForList(query));
            }
        }

        return result;
    }

    @Override
    public boolean insert(String query) {
        // TODO
        return true;
    }

    @Override
    public boolean update(String query) {
        // TODO
        return true;
    }

    @Override
    public boolean delete(String query) {
        // TODO
        return true;
    }
}
