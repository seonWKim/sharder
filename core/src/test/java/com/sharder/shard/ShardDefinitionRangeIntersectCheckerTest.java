package com.sharder.shard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

import com.sharder.Token;
import com.sharder.TokenType;

class ShardDefinitionRangeIntersectCheckerTest {

    @Test
    void greater_than_same_value() {
        Token operator1 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "10", "10", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void greater_than_bigger_value() {
        Token operator1 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "20", "20", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void greater_than_smaller_value() {
        Token operator1 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "5", "5", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void greater_than_or_equal_same_value() {
        Token operator1 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "10", "10", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.GREATER_THAN, ">", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void greater_than_or_equal_bigger_value() {
        Token operator1 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "20", "20", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.GREATER_THAN, ">", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void greater_than_or_equal_smaller_value() {
        Token operator1 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "5", "5", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.GREATER_THAN, ">", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void less_than_same_value() {
        Token operator1 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "10", "10", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void less_than_bigger_value() {
        Token operator1 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "20", "20", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void less_than_smaller_value() {
        Token operator1 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.LESS_THAN, "<", "<", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "5", "5", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void less_than_or_equal_same_value() {
        Token operator1 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "10", "10", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.LESS_THAN, "<", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void less_than_or_equal_bigger_value() {
        Token operator1 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "20", "20", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.LESS_THAN, "<", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "20", 20, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isFalse();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }

    @Test
    void less_than_or_equal_smaller_value() {
        Token operator1 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value1 = new Token(TokenType.NUMBER, "10", 10, Token.NOT_FROM_USER_INPUT);

        Token operator2 = new Token(TokenType.LESS_THAN_OR_EQUAL, "<=", "<=", Token.NOT_FROM_USER_INPUT);
        Token value2 = new Token(TokenType.NUMBER, "5", "5", Token.NOT_FROM_USER_INPUT);

        Token operator3 = new Token(TokenType.LESS_THAN, "<", 10, Token.NOT_FROM_USER_INPUT);
        Token value3 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator4 = new Token(TokenType.GREATER_THAN, ">", ">", Token.NOT_FROM_USER_INPUT);
        Token value4 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator5 = new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", ">=", Token.NOT_FROM_USER_INPUT);
        Token value5 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator6 = new Token(TokenType.EQUAL, "=", "=", Token.NOT_FROM_USER_INPUT);
        Token value6 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        Token operator7 = new Token(TokenType.NOT_EQUAL, "!=", "!=", Token.NOT_FROM_USER_INPUT);
        Token value7 = new Token(TokenType.NUMBER, "5", 5, Token.NOT_FROM_USER_INPUT);

        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator2, value2)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator3, value3)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator4, value4)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator5, value5)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator6, value6)).isTrue();
        assertThat(ShardDefinitionRangeIntersectChecker.intersects(operator1, value1, operator7, value7)).isTrue();
    }
}
