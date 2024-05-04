package com.sharder.shard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.sharder.Token;
import com.sharder.TokenType;
import com.sharder.shard.ShardDefinitionRange.ColumnRangeCondition;
import com.sharder.shard.ShardDefinitionRange.ColumnRangeConditions;

class RangeIntersectCheckerTest {

    final Token column = new Token(TokenType.IDENTIFIER, "id", "id", Token.NOT_FROM_USER_INPUT);
    final Token greaterThanOp = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
    final Token greaterThanOrEqualOp = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=",
                                                 Token.NOT_FROM_USER_INPUT);
    final Token lessThanOp = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
    final Token lessThanOrEqualOp = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=",
                                              Token.NOT_FROM_USER_INPUT);

    final Token equalOp = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);

    @Test
    void single_condition_intersection_test() {
        ColumnRangeConditions conditions1;
        ColumnRangeConditions conditions2;

        conditions1 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOp, numberToken("10")));
        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("20")));
        assertThat(RangeIntersectChecker.intersects(conditions1, conditions2)).isTrue();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions1)).isTrue();

        conditions1 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOrEqualOp, numberToken("10")));
        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("10")));
        assertThat(RangeIntersectChecker.intersects(conditions1, conditions2)).isTrue();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions1)).isTrue();

        conditions1 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOp, numberToken("10")));
        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("10")));
        assertThat(RangeIntersectChecker.intersects(conditions1, conditions2)).isFalse();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions1)).isFalse();
    }

    @Test
    void multiple_condition_intersection_test() {
        ColumnRangeConditions conditions1;
        ColumnRangeConditions conditions2;

        conditions1 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOp, numberToken("10")),
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("20")));
        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("30")),
                new ColumnRangeCondition(column, greaterThanOrEqualOp, numberToken("15")));
        assertThat(RangeIntersectChecker.intersects(conditions1, conditions2)).isTrue();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions1)).isTrue();

        conditions1 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOp, numberToken("10")),
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("20")));
        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("30")),
                new ColumnRangeCondition(column, greaterThanOrEqualOp, numberToken("20")));
        assertThat(RangeIntersectChecker.intersects(conditions1, conditions2)).isTrue();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions1)).isTrue();

        conditions1 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOp, numberToken("10")),
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("20")));
        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("30")),
                new ColumnRangeCondition(column, greaterThanOp, numberToken("20")));
        assertThat(RangeIntersectChecker.intersects(conditions1, conditions2)).isFalse();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions1)).isFalse();

        conditions1 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOp, numberToken("10")),
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("20")));
        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("40")),
                new ColumnRangeCondition(column, greaterThanOp, numberToken("30")));
        assertThat(RangeIntersectChecker.intersects(conditions1, conditions2)).isFalse();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions1)).isFalse();
    }

    @Test
    void equal_condition_intersection_test() {
        ColumnRangeConditions conditions;
        ColumnRangeConditions conditions2;

        conditions = new ColumnRangeConditions(
                new ColumnRangeCondition(column, greaterThanOrEqualOp, numberToken("10")),
                new ColumnRangeCondition(column, lessThanOp, numberToken("20")));

        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, equalOp, numberToken("5")));
        assertThat(RangeIntersectChecker.intersects(conditions, conditions2)).isFalse();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions)).isFalse();

        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, equalOp, numberToken("10")));
        assertThat(RangeIntersectChecker.intersects(conditions, conditions2)).isTrue();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions)).isTrue();

        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, equalOp, numberToken("20")));
        assertThat(RangeIntersectChecker.intersects(conditions, conditions2)).isFalse();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions)).isFalse();

        conditions2 = new ColumnRangeConditions(
                new ColumnRangeCondition(column, equalOp, numberToken("30")));
        assertThat(RangeIntersectChecker.intersects(conditions, conditions2)).isFalse();
        assertThat(RangeIntersectChecker.intersects(conditions2, conditions)).isFalse();
    }

    // TODO: add support for not_equal condition
//    @Test
//    void not_equal_condition_intersection_test() {
//        ColumnRangeConditions conditions;
//        ColumnRangeConditions conditions2;
//
//        conditions = new ColumnRangeConditions(
//                new ColumnRangeCondition(column, greaterThanOrEqualOp, numberToken("10")),
//                new ColumnRangeCondition(column, lessThanOrEqualOp, numberToken("10")));
//
//        conditions2 = new ColumnRangeConditions(
//                new ColumnRangeCondition(column, new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT), numberToken("5")));
//        assertThat(ShardDefinitionRangeIntersectChecker.intersects(conditions, conditions2)).isTrue();
//        assertThat(ShardDefinitionRangeIntersectChecker.intersects(conditions2, conditions)).isTrue();
//
//        conditions2 = new ColumnRangeConditions(
//                new ColumnRangeCondition(column, new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT), numberToken("10")));
//        assertThat(ShardDefinitionRangeIntersectChecker.intersects(conditions, conditions2)).isFalse();
//        assertThat(ShardDefinitionRangeIntersectChecker.intersects(conditions2, conditions)).isFalse();
//
//        conditions2 = new ColumnRangeConditions(
//                new ColumnRangeCondition(column, new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT), numberToken("20")));
//        assertThat(ShardDefinitionRangeIntersectChecker.intersects(conditions, conditions2)).isTrue();
//        assertThat(ShardDefinitionRangeIntersectChecker.intersects(conditions2, conditions)).isTrue();
//    }

    private Token numberToken(String number) {
        return new Token(TokenType.NUMBER, number, new BigDecimal(number), Token.NOT_FROM_USER_INPUT);
    }
}
