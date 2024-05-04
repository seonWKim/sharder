package io.github.seonWKim.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.seonWKim.config.record.SharderDatabaseImpl;
import io.github.seonWKim.SimpleQueryShardMatcher;
import io.github.seonWKim.config.record.SharderDatabases;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueryServiceImpl implements QueryService {

    private final List<SharderDatabaseImpl> databases;
    private final SimpleQueryShardMatcher shardMatcher = new SimpleQueryShardMatcher();

    public QueryServiceImpl(SharderDatabases databases) {
        this.databases = new ArrayList<>(databases.configs().values());
    }

    @Override
    public List<Map<String, Object>> select(String query) {
        final List<SharderDatabaseImpl> matchedDatabases = findMatchedDatabases(query);
        final List<Map<String, Object>> result = new ArrayList<>();
        for (var database : matchedDatabases) {
            log.info("Querying database: {}", database.databaseName());
            final List<Map<String, Object>> queryResult = database.jdbcTemplate().queryForList(query);
            result.addAll(queryResult);
            log.info("Queried database: {}", database.databaseName());
        }

        return result;
    }

    @Override
    public boolean insert(String query) {
        final List<SharderDatabaseImpl> matchedDatabases = findMatchedDatabases(query);
        for (var database : matchedDatabases) {
            log.info("Inserting into database: {}", database.databaseName());
            database.jdbcTemplate().execute(query);
            log.info("Inserted into database: {}", database.databaseName());
        }

        return true;
    }

    @Override
    public boolean update(String query) {
        final List<SharderDatabaseImpl> matchedDatabases = findMatchedDatabases(query);
        for (var database : matchedDatabases) {
            log.info("Updating database: {}", database.databaseName());
            final int result = database.jdbcTemplate().update(query);
            log.info("Updated {} rows in database: {}", result, database.databaseName());
        }

        return true;
    }

    @Override
    public boolean delete(String query) {
        final List<SharderDatabaseImpl> matchedDatabases = findMatchedDatabases(query);
        for (var database : matchedDatabases) {
            log.info("Deleting from database: {}", database.databaseName());
            final int result = database.jdbcTemplate().update(query);
            log.info("Deleted {} rows from database: {}", result, database.databaseName());
        }
        return true;
    }

    private List<SharderDatabaseImpl> findMatchedDatabases(String query) {
        return shardMatcher.match(query, databases);
    }
}
