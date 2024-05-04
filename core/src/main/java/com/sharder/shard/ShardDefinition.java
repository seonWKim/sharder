package com.sharder.shard;

import java.util.List;
import java.util.Set;

import com.sharder.Expression;
import com.sharder.Nullable;
import com.sharder.Token;
import com.sharder.TokenType;

/**
 * Represents a shard definition.
 */
public interface ShardDefinition {

    /**
     * The type of the shard definition.
     */
    ShardDefinitionType type();

    /**
     * The name of the table that this shard definition applies to.
     */
    @Nullable
    String tableName();

    /**
     * The name of the column that this shard definition applies to.
     */
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

