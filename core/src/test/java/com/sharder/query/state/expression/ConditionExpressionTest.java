package com.sharder.query.state.expression;

import static com.sharder.TokenType.IDENTIFIER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.sharder.Token;
import com.sharder.TokenType;
import com.sharder.query.state.expr.ConditionExpression;

class ConditionExpressionTest {

    @Test
    void equal_expression() {
        // id = 10
        var tokens = List.of(
                new Token(IDENTIFIER, "id", "id", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "10", 10, 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();
        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(0));
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(1));
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(2));
    }

    @Test
    void double_equal_expression() {
        // id = 10 AND age = 20
        var tokens = List.of(
                new Token(IDENTIFIER, "id", "id", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "10", 10, 1),
                new Token(TokenType.AND, "AND", "AND", 1),
                new Token(IDENTIFIER, "age", "age", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "20", 20, 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();
        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(0));
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(1));
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(2));
        assertThat(result.get(3).getToken()).isEqualTo(tokens.get(3));
        assertThat(result.get(4).getToken()).isEqualTo(tokens.get(4));
        assertThat(result.get(5).getToken()).isEqualTo(tokens.get(5));
        assertThat(result.get(6).getToken()).isEqualTo(tokens.get(6));
    }

    @Test
    void multiple_operators() {
        // id = 10 + 5 - 2
        var tokens = List.of(
                new Token(IDENTIFIER, "id", "id", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "10", 10, 1),
                new Token(TokenType.PLUS, "+", "+", 1),
                new Token(TokenType.NUMBER, "5", 5, 1),
                new Token(TokenType.MINUS, "-", "-", 1),
                new Token(TokenType.NUMBER, "2", 2, 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();
        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(0));
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(1));
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(2));
        assertThat(result.get(3).getToken()).isEqualTo(tokens.get(3));
        assertThat(result.get(4).getToken()).isEqualTo(tokens.get(4));
        assertThat(result.get(5).getToken()).isEqualTo(tokens.get(5));
        assertThat(result.get(6).getToken()).isEqualTo(tokens.get(6));
    }

    @Test
    void parenthesis_1() {
        // (i = 0)
        var tokens = List.of(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(IDENTIFIER, "i", "i", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "0", 0, 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();
        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(1)); // i
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(2)); // =
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(3)); // 10
    }

    @Test
    void parenthesis_2() {
        // (( i = 10 ))
        var tokens = List.of(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(IDENTIFIER, "i", "i", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "10", 10, 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();
        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(2)); // i
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(3)); // =
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(4)); // 10
    }

    @Test
    void parenthesis_3() {
        // (i = 0) AND (j = 10)
        var tokens = List.of(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(IDENTIFIER, "i", "i", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "0", 0, 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1),
                new Token(TokenType.AND, "AND", "AND", 1),
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(IDENTIFIER, "j", "j", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "10", 10, 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();

        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(1)); // i
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(2)); // =
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(3)); // 10

        assertThat(result.get(3).getToken()).isEqualTo(tokens.get(5)); // AND

        assertThat(result.get(4).getToken()).isEqualTo(tokens.get(7)); // j
        assertThat(result.get(5).getToken()).isEqualTo(tokens.get(8)); // =
        assertThat(result.get(6).getToken()).isEqualTo(tokens.get(9)); // 10
    }

    @Test
    void parenthesis_4() {
        // i = 0 OR (j > 10 AND k < 20)
        var tokens = List.of(
                new Token(IDENTIFIER, "i", "i", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "0", 0, 1),
                new Token(TokenType.OR, "OR", "OR", 1),
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(IDENTIFIER, "j", "j", 1),
                new Token(TokenType.GREATER_THAN, ">", ">", 1),
                new Token(TokenType.NUMBER, "10", 10, 1),
                new Token(TokenType.AND, "AND", "AND", 1),
                new Token(IDENTIFIER, "k", "k", 1),
                new Token(TokenType.LESS_THAN, "<", "<", 1),
                new Token(TokenType.NUMBER, "20", 20, 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();
        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(0)); // i
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(1)); // =
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(2)); // 10
        
        assertThat(result.get(3).getToken()).isEqualTo(tokens.get(3)); // OR

        assertThat(result.get(4).getToken()).isEqualTo(tokens.get(5)); // i
        assertThat(result.get(5).getToken()).isEqualTo(tokens.get(6)); // >
        assertThat(result.get(6).getToken()).isEqualTo(tokens.get(7)); // 10

        assertThat(result.get(7).getToken()).isEqualTo(tokens.get(8)); // AND

        assertThat(result.get(8).getToken()).isEqualTo(tokens.get(9)); // i
        assertThat(result.get(9).getToken()).isEqualTo(tokens.get(10)); // <
        assertThat(result.get(10).getToken()).isEqualTo(tokens.get(11)); // 20
    }

    @Test
    void parenthesis_5() {
        // i = 10 OR (j = 20 AND k = 30) OR l = 40
        var tokens = List.of(
                new Token(IDENTIFIER, "i", "i", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "10", 10, 1),
                new Token(TokenType.OR, "OR", "OR", 1),
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(IDENTIFIER, "j", "j", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "20", 20, 1),
                new Token(TokenType.AND, "AND", "AND", 1),
                new Token(IDENTIFIER, "k", "k", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "30", 30, 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1),
                new Token(TokenType.OR, "OR", "OR", 1),
                new Token(IDENTIFIER, "l", "l", 1),
                new Token(TokenType.EQUAL, "=", "=", 1),
                new Token(TokenType.NUMBER, "40", 40, 1)
        );

        var expr = new ConditionExpression(tokens);
        var tree = expr.getTree();
        var result = tree.preOrderTraversal();
        assertThat(result.get(0).getToken()).isEqualTo(tokens.get(0)); // i
        assertThat(result.get(1).getToken()).isEqualTo(tokens.get(1)); // =
        assertThat(result.get(2).getToken()).isEqualTo(tokens.get(2)); // 10

        assertThat(result.get(3).getToken()).isEqualTo(tokens.get(3)); // OR

        assertThat(result.get(4).getToken()).isEqualTo(tokens.get(5)); // j
        assertThat(result.get(5).getToken()).isEqualTo(tokens.get(6)); // =
        assertThat(result.get(6).getToken()).isEqualTo(tokens.get(7)); // 20

        assertThat(result.get(7).getToken()).isEqualTo(tokens.get(8)); // AND

        assertThat(result.get(8).getToken()).isEqualTo(tokens.get(9)); // k
        assertThat(result.get(9).getToken()).isEqualTo(tokens.get(10)); // =
        assertThat(result.get(10).getToken()).isEqualTo(tokens.get(11)); // 30

        assertThat(result.get(11).getToken()).isEqualTo(tokens.get(13)); // OR

        assertThat(result.get(12).getToken()).isEqualTo(tokens.get(14)); // l
        assertThat(result.get(13).getToken()).isEqualTo(tokens.get(15)); // =
        assertThat(result.get(14).getToken()).isEqualTo(tokens.get(16)); // 40
    }
}
