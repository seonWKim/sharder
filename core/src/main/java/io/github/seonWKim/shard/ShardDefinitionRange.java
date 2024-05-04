package io.github.seonWKim.shard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.github.seonWKim.Expression;
import io.github.seonWKim.Nullable;
import io.github.seonWKim.Token;
import io.github.seonWKim.TokenType;
import io.github.seonWKim.query.state.expr.ConditionExpression;
import io.github.seonWKim.query.state.expr.ConditionExpression.ConditionNode;

import lombok.Getter;

/**
 * Represents a shard definition using range operation.<br><br>
 * type1:<br>
 * - {@code table_name.column_name (< | <= | > | >=) value}<br>
 * - e.g. {@code users.id > 10}<br><br>
 * type2: Note that we don't support OR here. You can allow OR operations by defining multiple {@link ShardDefinitionRange}s.<br>
 * - {@code table_name.column_name (< | <= | > | >=) value AND table_name.column_name (< | <= | > | >=) value}<br>
 * - e.g. {@code users.id > 10 AND users.id <= 20}<br>
 */
@Getter
public class ShardDefinitionRange implements ShardDefinition {
    private final String definitionStr;
    private final Token table;
    private final Token column;
    private final ColumnRangeConditions conditions;

    private static final Set<TokenType> supportedOperators =
            Set.of(TokenType.GREATER_THAN, TokenType.GREATER_THAN_OR_EQUAL,
                   TokenType.LESS_THAN, TokenType.LESS_THAN_OR_EQUAL);
    private static final List<Set<TokenType>> shardDefinitionValidator =
            List.of(Set.of(TokenType.IDENTIFIER), Set.of(TokenType.DOT), Set.of(TokenType.IDENTIFIER),
                    supportedOperators, Set.of(TokenType.NUMBER, TokenType.STRING));

    private static final List<Set<TokenType>> type1Validator = new ArrayList<>();
    private static final List<Set<TokenType>> type2Validator = new ArrayList<>();

    static {
        type1Validator.addAll(shardDefinitionValidator);
        type1Validator.add(Set.of(TokenType.EOF));

        type2Validator.addAll(shardDefinitionValidator);
        type2Validator.add(Set.of(TokenType.AND));
        type2Validator.addAll(shardDefinitionValidator);
        type2Validator.add(Set.of(TokenType.EOF));
    }

    public ShardDefinitionRange(String definitionStr) {
        this.definitionStr = definitionStr;
        List<Token> tokens = new ShardDefinitionScanner(definitionStr).scanTokens();

        if (validate(tokens, type1Validator)) {
            this.table = tokens.get(0);
            this.column = tokens.get(2);
            this.conditions = new ColumnRangeConditions(
                    new ColumnRangeCondition(column, tokens.get(3), tokens.get(4)));
        } else if (validate(tokens, type2Validator)) {
            if (!Objects.equals(tokens.get(0), tokens.get(6))) {
                throw new IllegalArgumentException(
                        "Invalid shard definition, table name should be the same: " + definitionStr);
            }
            this.table = tokens.get(0);

            if (!Objects.equals(tokens.get(2).lexeme(), tokens.get(8).lexeme())) {
                throw new IllegalArgumentException(
                        "Invalid shard definition, column name should be the same: " + definitionStr);
            }
            this.column = tokens.get(2);

            this.conditions = new ColumnRangeConditions(
                    new ColumnRangeCondition(column, tokens.get(3), tokens.get(4)),
                    new ColumnRangeCondition(column, tokens.get(9), tokens.get(10)));
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
                "Unsupported expression type: " + conditionExpression.getClass());
    }

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

            // identifiers are not equal, so we can skip the check
            if (!Objects.equals(identifier.getToken().lexeme(), column.lexeme())) {
                return true;
            }

            final ConditionNode valueNode = condition.getRight();
            if (valueNode == null) {
                throw new IllegalArgumentException("Right node(value) should not be null");
            } else if (valueNode.getToken().type() != valueNode.getToken().type()) {
                throw new IllegalArgumentException(
                        "Value types mismatch: {} != {}" + valueNode.getToken().type() + valueNode.getToken()
                                                                                                  .type());
            }

            final ColumnRangeConditions conditions = new ColumnRangeConditions(
                    new ColumnRangeCondition(column, condition.getToken(), valueNode.getToken()));
            return RangeIntersectChecker.intersects(conditions, this.conditions);
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

    @Getter
    public static class ColumnRangeConditions {
        private final ColumnRangeCondition left;
        private final ColumnRangeCondition right;

        @Nullable
        private final ColumnRangeCondition notEqualValue;

        private static final Set<TokenType> lessThenOperators = Set.of(TokenType.LESS_THAN,
                                                                       TokenType.LESS_THAN_OR_EQUAL);
        private static final Set<TokenType> greaterThenOperators = Set.of(TokenType.GREATER_THAN,
                                                                          TokenType.GREATER_THAN_OR_EQUAL);

        private static final Token greaterThanEqualOp = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=",
                                                                  ">=", Token.NOT_FROM_USER_INPUT);
        private static final Token lessThanEqualOp = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=",
                                                               "<=", Token.NOT_FROM_USER_INPUT);

        public ColumnRangeConditions(ColumnRangeCondition cond) {
            switch (cond.operator.type()) {
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL:
                    this.right = cond;
                    this.left = ColumnRangeCondition.greaterThanOrEqualMinusInfinite(cond.column);
                    this.notEqualValue = null;
                    break;
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL:
                    this.left = cond;
                    this.right = ColumnRangeCondition.lessThanOrEqualPlusInfinite(cond.column);
                    this.notEqualValue = null;
                    break;
                case EQUAL:
                    this.left = new ColumnRangeCondition(cond.column, greaterThanEqualOp, cond.value);
                    this.right = new ColumnRangeCondition(cond.column, lessThanEqualOp, cond.value);
                    this.notEqualValue = null;
                    break;
                case NOT_EQUAL:
                    this.left = ColumnRangeCondition.greaterThanOrEqualMinusInfinite(cond.column);
                    this.right = ColumnRangeCondition.lessThanOrEqualPlusInfinite(cond.column);
                    this.notEqualValue = cond;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + cond.operator.lexeme());
            }
        }

        public ColumnRangeConditions(ColumnRangeCondition left, ColumnRangeCondition right) {
            if (!isOppositeOperator(left.operator.type(), right.operator.type())) {
                throw new IllegalArgumentException(
                        "Invalid operators: " + left.operator.lexeme() + " " + right.operator.lexeme());
            }

            if (greaterThenOperators.contains(left.operator.type())) {
                this.left = left;
                this.right = right;
            } else {
                this.left = right;
                this.right = left;
            }
            this.notEqualValue = null;
        }

        private boolean isOppositeOperator(TokenType type1, TokenType type2) {
            return (lessThenOperators.contains(type1) && greaterThenOperators.contains(type2)) ||
                   (greaterThenOperators.contains(type1) && lessThenOperators.contains(type2));
        }
    }

    @Getter
    public static class ColumnRangeCondition {
        private final Token column;
        private final Token operator;
        private final Token value;

        private static final BigDecimal MINUS_INFINITY = new BigDecimal(Long.MIN_VALUE);
        private static final BigDecimal PLUS_INFINITY = new BigDecimal(Long.MAX_VALUE);

        public static ColumnRangeCondition greaterThanOrEqualMinusInfinite(Token column) {
            return new ColumnRangeCondition(column,
                                            new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=",
                                                      ">", Token.NOT_FROM_USER_INPUT),
                                            new Token(TokenType.NUMBER, MINUS_INFINITY.toString(),
                                                      MINUS_INFINITY, Token.NOT_FROM_USER_INPUT));
        }

        public static ColumnRangeCondition lessThanOrEqualPlusInfinite(Token column) {
            return new ColumnRangeCondition(column,
                                            new Token(TokenType.LESS_THAN_OR_EQUAL, "<=",
                                                      "<=", Token.NOT_FROM_USER_INPUT),
                                            new Token(TokenType.NUMBER, PLUS_INFINITY.toString(),
                                                      PLUS_INFINITY, Token.NOT_FROM_USER_INPUT));
        }

        public ColumnRangeCondition(Token column, Token operator, Token value) {
            this.column = column;
            this.operator = operator;
            this.value = value;
        }

        public boolean includeValue() {
            return operator.type() == TokenType.GREATER_THAN_OR_EQUAL ||
                   operator.type() == TokenType.LESS_THAN_OR_EQUAL;
        }

        public String value() {
            return value.lexeme();
        }
    }
}
