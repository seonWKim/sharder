package com.sharder.shard;

import java.util.List;
import java.util.Set;

import com.sharder.Expression;
import com.sharder.Nullable;
import com.sharder.Token;
import com.sharder.TokenType;
import com.sharder.query.state.expr.ConditionExpression;
import com.sharder.query.state.expr.ConditionExpression.ConditionNode;

import lombok.Getter;

/**
 * table_name.column_name (< | <= | > | >=) value
 */
@Getter
public class ShardDefinitionRange implements ShardDefinition {
    private final String definitionStr;
    private final Token table;
    private final Token column;
    private final Token operator;
    private final Token value;

    // e.g. table_name.column_name < 10
    private final Set<TokenType> supportedOperators = Set.of(TokenType.GREATER_THAN,
                                                             TokenType.GREATER_THAN_OR_EQUAL,
                                                             TokenType.LESS_THAN, TokenType.LESS_THAN_OR_EQUAL);
    private final List<Set<TokenType>> validator = List.of(Set.of(TokenType.IDENTIFIER), Set.of(TokenType.DOT),
                                                           Set.of(TokenType.IDENTIFIER), supportedOperators,
                                                           Set.of(TokenType.NUMBER, TokenType.STRING),
                                                           Set.of(TokenType.EOF));

    public ShardDefinitionRange(String definitionStr) {
        this.definitionStr = definitionStr;
        List<Token> tokens = new ShardDefinitionScanner(definitionStr).scanTokens();

        if (validate(tokens, validator)) {
            this.table = tokens.get(0);
            this.column = tokens.get(2);
            this.operator = tokens.get(3);
            this.value = tokens.get(4);
        } else {
            throw new IllegalArgumentException("Invalid shard definition: " + definitionStr);
        }
    }

    @Override
    public ShardDefinitionType type() {
        return ShardDefinitionType.RANGE;
    }

    @Override
    public String tableName() {
        return table.lexeme();
    }

    @Override
    public String columnName() {
        return column.lexeme();
    }

    @Override
    public boolean match(Expression conditionExpression) {
        if (conditionExpression instanceof ConditionExpression expression) {
            return match(expression.getTree().getRoot());
        }

        throw new IllegalArgumentException(
                "Unsupported expression type: " + conditionExpression.getExpressionType());
    }

    private boolean match(@Nullable ConditionNode condition) {
        if (condition == null) {
            return true;
        }

        final TokenType type = condition.getToken().type();
        if (condition.isSupportedOperator() && supportedOperators.contains(type)) {
            final ConditionNode identifier = condition.getLeft();
            if (identifier == null) {
                throw new IllegalArgumentException("Left node(identifier) should not be null");
            }
            if (!identifier.getToken().lexeme().equals(column.lexeme())) {
                return true;
            }

            final ConditionNode value = condition.getRight();
            if (value == null) {
                throw new IllegalArgumentException("Right node(value) should not be null");
            } else if (value.getToken().type() != value.getToken().type()) {
                throw new IllegalArgumentException(
                        "Value types mismatch: {} != {}" + value.getToken().type() + value.getToken().type());
            }


        }

        if (condition.isLogicalOperator()) {
            return switch (type) {
                case AND -> match(condition.getLeft()) && match(condition.getRight());
                case OR -> match(condition.getLeft()) || match(condition.getRight());
                default -> throw new IllegalArgumentException("Unsupported logical operator: " + type);
            };
        }

        throw new IllegalArgumentException("Should not reach here: " + type);
    }
}
