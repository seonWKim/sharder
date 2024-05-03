package com.sharder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sharder.SimpleQueryService;
import com.sharder.SimpleQueryShardMatcher;
import com.sharder.config.record.SharderDatabaseImpl;
import com.sharder.config.record.SharderDatabases;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimpleQueryServiceImpl implements SimpleQueryService {

    private final List<SharderDatabaseImpl> databases;
    private final SimpleQueryShardMatcher shardMatcher = new SimpleQueryShardMatcher();

    public SimpleQueryServiceImpl(SharderDatabases databases) {
        this.databases = new ArrayList<>(databases.configs().values());
    }

    @Override
    public List<Map<String, Object>> select(String query) {
        final List<SharderDatabaseImpl> matchedDatabases = shardMatcher.match(query, databases);

        final List<Map<String, Object>> result = new ArrayList<>();
        for (var database : matchedDatabases) {
            log.info("Querying database: {}", database.databaseName());
            result.addAll(database.jdbcTemplate().queryForList(query));

        }

        return result;
    }

    @Override
    public boolean insert(String query) {
        final List<SharderDatabaseImpl> matchedDatabases = shardMatcher.match(query, databases);

        for (var database : matchedDatabases) {
            log.info("Inserting into database: {}", database.databaseName());
            database.jdbcTemplate().execute(query);
        }

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
