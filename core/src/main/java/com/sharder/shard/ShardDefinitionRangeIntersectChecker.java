package com.sharder.shard;

import java.util.Set;

import com.sharder.Token;
import com.sharder.TokenType;

public class ShardDefinitionRangeIntersectChecker {
    private static final Set<TokenType> conditionSupportedOperators =
            Set.of(TokenType.GREATER_THAN, TokenType.GREATER_THAN_OR_EQUAL, TokenType.LESS_THAN,
                   TokenType.LESS_THAN_OR_EQUAL);

    private static final Set<TokenType> shardSupportedOperators =
            Set.of(TokenType.GREATER_THAN, TokenType.GREATER_THAN_OR_EQUAL, TokenType.LESS_THAN,
                   TokenType.LESS_THAN_OR_EQUAL, TokenType.EQUAL, TokenType.NOT_EQUAL);

    public static boolean intersects(Token operator1, Token value1, Token operator2, Token value2) {
        if (!conditionSupportedOperators.contains(operator1.type())) {
            throw new IllegalArgumentException("Unsupported operator1: " + operator1);
        }

        if (!shardSupportedOperators.contains(operator2.type())) {
            throw new IllegalArgumentException("Unsupported operator2: " + operator2);
        }

        switch (operator1.type()) {
            case GREATER_THAN:
                switch (operator2.type()) {
                    case GREATER_THAN, GREATER_THAN_OR_EQUAL, NOT_EQUAL:
                        return true;
                    case LESS_THAN, LESS_THAN_OR_EQUAL, EQUAL:
                        return value1.compareTo(value2) < 0;
                    default:
                        throw new IllegalArgumentException("Unsupported operator: " + operator2);
                }
            case GREATER_THAN_OR_EQUAL:
                switch (operator2.type()) {
                    case GREATER_THAN, GREATER_THAN_OR_EQUAL, NOT_EQUAL:
                        return true;
                    case LESS_THAN:
                        return value1.compareTo(value2) < 0;
                    case LESS_THAN_OR_EQUAL, EQUAL:
                        return value1.compareTo(value2) <= 0;
                    default:
                        throw new IllegalArgumentException("Unsupported operator: " + operator2);
                }
            case LESS_THAN:
                switch (operator2.type()) {
                    case LESS_THAN, LESS_THAN_OR_EQUAL, NOT_EQUAL:
                        return true;
                    case GREATER_THAN, GREATER_THAN_OR_EQUAL, EQUAL:
                        return value1.compareTo(value2) > 0;
                    default:
                        throw new IllegalArgumentException("Unsupported operator: " + operator2);
                }
            case LESS_THAN_OR_EQUAL:
                switch (operator2.type()) {
                    case LESS_THAN, LESS_THAN_OR_EQUAL, NOT_EQUAL:
                        return true;
                    case GREATER_THAN:
                        return value1.compareTo(value2) > 0;
                    case GREATER_THAN_OR_EQUAL, EQUAL:
                        return value1.compareTo(value2) >= 0;
                    default:
                        throw new IllegalArgumentException("Unsupported operator: " + operator2);
                }
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator1);
        }
    }
}
