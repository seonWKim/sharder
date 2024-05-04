package io.github.seonWKim;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    void number_comparison() {
        Token token1 = new Token(TokenType.NUMBER, "1", 1, Token.NOT_FROM_USER_INPUT);
        Token token2 = new Token(TokenType.NUMBER, "2", 1, Token.NOT_FROM_USER_INPUT);
        Token token3 = new Token(TokenType.NUMBER, "1", 1, Token.NOT_FROM_USER_INPUT);

        assertThat(token1.compareTo(token2)).isNegative();
        assertThat(token2.compareTo(token1)).isPositive();
        assertThat(token1.compareTo(token3)).isZero();
    }

    @Test
    void string_comparison() {
        Token token1 = new Token(TokenType.STRING, "a", 1, Token.NOT_FROM_USER_INPUT);
        Token token2 = new Token(TokenType.STRING, "b", 1, Token.NOT_FROM_USER_INPUT);
        Token token3 = new Token(TokenType.STRING, "a", 1, Token.NOT_FROM_USER_INPUT);

        assertThat(token1.compareTo(token2)).isNegative();
        assertThat(token2.compareTo(token1)).isPositive();
        assertThat(token1.compareTo(token3)).isZero();
    }
}
