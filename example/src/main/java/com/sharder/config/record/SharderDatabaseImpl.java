package com.sharder.config.record;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;

import com.sharder.shard.ShardDefinition;
import com.sharder.shard.SharderDatabase;
import com.zaxxer.hikari.HikariDataSource;

public class SharderDatabaseImpl implements SharderDatabase {
    private final String databaseName;
    private final List<ShardDefinition> shardDefinitions;
    private final HikariDataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String, List<ShardDefinition>> shardDefinitionsByTableName;

    public SharderDatabaseImpl(String databaseName, List<ShardDefinition> shardDefinitions,
                               HikariDataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.databaseName = databaseName;
        this.shardDefinitions = shardDefinitions;
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.shardDefinitionsByTableName = shardDefinitions.stream().collect(
                Collectors.groupingBy(ShardDefinition::tableName));
    }

    @Override
    public String databaseName() {
        return databaseName;
    }

    @Override
    public List<ShardDefinition> shardDefinitions() {
        return shardDefinitions;
    }

    @Override
    public Map<String, List<ShardDefinition>> shardDefinitionsByTableName() {
        return shardDefinitionsByTableName;
    }

    public JdbcTemplate jdbcTemplate() {
        return jdbcTemplate;
    }
}
