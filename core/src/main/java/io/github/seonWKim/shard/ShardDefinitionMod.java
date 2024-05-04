package io.github.seonWKim.shard;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.github.seonWKim.Expression;
import io.github.seonWKim.Nullable;
import io.github.seonWKim.query.state.expr.ConditionExpression;
import io.github.seonWKim.query.state.expr.ConditionExpression.ConditionNode;
import io.github.seonWKim.Token;
import io.github.seonWKim.TokenType;

import lombok.Getter;

/**
 * Represents a shard definition using modulo operation.
 * e.g. table_name.column_name % 2 = 0
 */
@Getter
public class ShardDefinitionMod implements ShardDefinition {
    private final String definitionStr;
    private final Token table;
    private final Token column;
    private final Token divisor;
    private final Token result;

    // e.g. table_name.column_name % 2 = 0
    private final List<Set<TokenType>> validator = List.of(
            Set.of(TokenType.IDENTIFIER), Set.of(TokenType.DOT), Set.of(TokenType.IDENTIFIER),
            Set.of(TokenType.MOD), Set.of(TokenType.NUMBER), Set.of(TokenType.EQUAL),
            Set.of(TokenType.NUMBER), Set.of(TokenType.EOF));

    public ShardDefinitionMod(String definitionStr) {
        this.definitionStr = definitionStr;

        List<Token> tokens = new ShardDefinitionScanner(definitionStr).scanTokens();
        if (validate(tokens, validator)) {
            this.table = tokens.get(0);
            this.column = tokens.get(2);
            this.divisor = tokens.get(4);
            this.result = tokens.get(6);
        } else {
            throw new IllegalArgumentException("Invalid shard definition: " + definitionStr);
        }
    }

    @Override
    public ShardDefinitionType type() {
        return ShardDefinitionType.MOD;
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

        throw new IllegalArgumentException("Unsupported expression type: " + conditionExpression.getClass());
    }

    /**
     * Only supports condition node with logical operators of "=", "!=".
     */
    private boolean match(@Nullable ConditionExpression.ConditionNode condition) {
        if (condition == null) {
            return true;
        }

        final TokenType type = condition.getToken().type();
        if (condition.isSupportedOperator()) {
            final ConditionNode identifier = condition.getLeft();
            if (identifier == null) {
                throw new IllegalArgumentException("Left node(identifier) should not be null");
            }
            if (!Objects.equals(identifier.getToken().lexeme(), (column.lexeme()))) {
                return true;
            }

            final ConditionNode value = condition.getRight();
            if (value == null) {
                throw new IllegalArgumentException("Right node(value) should not be null");
            }

            return switch (type) {
                case EQUAL -> matchEqual(identifier, value);
                case NOT_EQUAL -> matchNotEqual(identifier, value);
                default -> throw new IllegalArgumentException("Unsupported operator: " + type);
            };
        }

        if (condition.isLogicalOperator()) {
            return switch (type) {
                case AND -> match(condition.getLeft()) && match(condition.getRight());
                case OR -> match(condition.getLeft()) || match(condition.getRight());
                default -> throw new IllegalArgumentException("Unsupported logical operator: " + type);
            };
        }

        // is it possible to reach this place?
        throw new IllegalArgumentException("Should not reach here: " + condition);
    }

    private boolean matchEqual(ConditionNode identifier, ConditionNode value) {
        if (identifier.getToken().type() != TokenType.IDENTIFIER) {
            throw new IllegalArgumentException("Left node should be an identifier");
        }

        if (value.getToken().type() != TokenType.NUMBER) {
            throw new IllegalArgumentException("Right node should be a number");
        }

        if (Objects.equals(identifier.getToken().lexeme(), column.lexeme())) {
            return Integer.parseInt(value.getToken().lexeme()) % Integer.parseInt(divisor.lexeme())
                   == Integer.parseInt(result.lexeme());
        }

        return false;
    }

    private boolean matchNotEqual(ConditionNode identifier, ConditionNode value) {
        if (identifier.getToken().type() != TokenType.IDENTIFIER) {
            throw new IllegalArgumentException("Left node should be an identifier");
        }

        if (value.getToken().type() != TokenType.NUMBER) {
            throw new IllegalArgumentException("Right node should be a number");
        }

        if (Objects.equals(identifier.getToken().lexeme(), column.lexeme())) {
            return Integer.parseInt(value.getToken().lexeme()) % Integer.parseInt(divisor.lexeme())
                   != Integer.parseInt(result.lexeme());
        }

        return false;
    }
}
