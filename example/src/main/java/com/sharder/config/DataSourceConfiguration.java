package com.sharder.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sharder.config.SharderApplicationProperties.DataSourceInfo.DataSourceConfig;
import com.sharder.config.record.SharderDatabaseImpl;
import com.sharder.config.record.SharderDatabases;
import com.sharder.shard.ShardDefinition;
import com.sharder.shard.ShardDefinitionMod;
import com.sharder.shard.ShardDefinitionRange;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfiguration {

    private final SharderApplicationProperties sharderApplicationProperties;

    @Bean
    public SharderDatabases sharderDatabases() {
        final Map<String, SharderDatabaseImpl> dataSources = new HashMap<>();
        for (Map.Entry<String, SharderApplicationProperties.DataSourceInfo> entry : sharderApplicationProperties.datasource()
                                                                                                                .entrySet()) {
            final DataSourceConfig config = entry.getValue().config();
            final HikariDataSource dataSource = DataSourceBuilder.create()
                                                                 .type(HikariDataSource.class)
                                                                 .url(config.jdbcUrl())
                                                                 .username(config.username())
                                                                 .password(config.password())
                                                                 .build();
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            final String name = entry.getKey();
            final List<ShardDefinition> shardDefinitions =
                    entry.getValue().shard().stream().map(cfg -> {
                        switch (cfg.type()) {
                            case MOD:
                                return new ShardDefinitionMod(cfg.definition());
                            case RANGE:
                                return new ShardDefinitionRange(cfg.definition());
                            case HASH:
                                throw new UnsupportedOperationException("Hash sharding is not supported yet.");
                        }

                        throw new IllegalArgumentException("Invalid shard definition type: " + cfg.type());
                    }).collect(Collectors.toList());

            dataSources.put(name, new SharderDatabaseImpl(name, shardDefinitions, jdbcTemplate));
        }
        return new SharderDatabases(dataSources);
    }
}
