package com.sharder.shard;

import java.util.List;
import java.util.Set;

import com.sharder.Expression;
import com.sharder.Nullable;
import com.sharder.Token;
import com.sharder.TokenType;

public interface ShardDefinition {
    ShardDefinitionType type();

    @Nullable
    String tableName();

    String columnName();

    /**
     * Check if the given condition expression matches the shard definition
     */
    boolean match(Expression conditionExpression);

    default boolean validate(List<Token> tokens, List<Set<TokenType>> matcher) {
        if (tokens.size() != matcher.size()) {
            return false;
        }

        for (int i = 0; i < tokens.size(); i++) {
            if (!matcher.get(i).contains(tokens.get(i).type())) {
                return false;
            }
        }

        return true;
    }
}

