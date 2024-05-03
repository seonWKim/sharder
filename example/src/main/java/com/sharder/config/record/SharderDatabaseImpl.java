package com.sharder.config.record;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.sharder.shard.ShardDefinition;
import com.sharder.shard.SharderDatabase;
import com.zaxxer.hikari.HikariDataSource;

public record SharderDatabaseImpl(
        String databaseName,
        List<ShardDefinition> shardDefinitions,
        HikariDataSource dataSource,
        JdbcTemplate jdbcTemplate
) implements SharderDatabase {}
