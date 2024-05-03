package com.sharder.config.record;

import org.springframework.jdbc.core.JdbcTemplate;

import com.sharder.shard.SharderDatabase;
import com.zaxxer.hikari.HikariDataSource;

public record SharderDatabaseHolder(
        SharderDatabase sharderDatabase,
        HikariDataSource dataSource,
        JdbcTemplate jdbcTemplate
) {}
