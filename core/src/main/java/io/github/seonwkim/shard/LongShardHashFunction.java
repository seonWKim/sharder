package io.github.seonwkim.shard;

import io.github.seonwkim.Token;
import io.github.seonwkim.TokenType;

public class LongShardHashFunction implements ShardHashFunction<Long> {

    static final LongShardHashFunction IDENTITY = new LongShardHashFunction();

    @Override
    public Long hash(Token token) {
        if (token.type() != TokenType.NUMBER) {
            throw new IllegalArgumentException("Token must be of type NUMBER");
        }

        try {
            return Long.parseLong(token.lexeme());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Token must be a valid number: " + token.lexeme(), e);
        }
    }
}
