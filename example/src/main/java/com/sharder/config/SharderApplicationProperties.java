package com.sharder.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sharder.shard.ShardDefinitionType;

@ConfigurationProperties("sharder")
public record SharderApplicationProperties(Map<String, DataSourceInfo> datasource) {

    public record DataSourceInfo(DataSourceConfig config,
                                 List<ShardDefinitionConfig> shard) {

        public record DataSourceConfig(String jdbcUrl, String username, String password) {}

        public record ShardDefinitionConfig(String name, ShardDefinitionType type, String definition) {}
    }
}
