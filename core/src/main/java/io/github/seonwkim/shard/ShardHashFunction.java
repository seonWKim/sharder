package io.github.seonwkim.shard;

import io.github.seonwkim.Token;

public interface ShardHashFunction<T> {
    T hash(Token token);
}
