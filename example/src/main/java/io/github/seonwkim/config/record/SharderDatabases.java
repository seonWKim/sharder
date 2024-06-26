package io.github.seonwkim.config.record;

import java.util.Map;

import io.github.seonwkim.Nullable;

public record SharderDatabases(Map<String, SharderDatabaseImpl> configs) {
    public boolean exists(String key) {
        return configs.containsKey(key);
    }

    @Nullable
    public SharderDatabaseImpl database(String key) {
        return configs.get(key);
    }
}
