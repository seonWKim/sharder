package com.seonWKim.sharder;

import java.math.BigDecimal;

/**
 * Represents a token in the query string.
 */
public record Token(TokenType type, String lexeme, Object literal, int line) implements Comparable<Token> {
    public static final int NOT_FROM_USER_INPUT = -1;
    public static final Token EQUAL = new Token(TokenType.EQUAL, "=", null, NOT_FROM_USER_INPUT);
    public static final Token OR = new Token(TokenType.OR, "OR", null, NOT_FROM_USER_INPUT);
    public static final Token AND = new Token(TokenType.AND, "AND", null, NOT_FROM_USER_INPUT);

    @Override
    public int compareTo(Token o) {
        if (o.type != type) {
            throw new IllegalArgumentException("Cannot compare tokens of different types");
        }

        if (o.type != TokenType.NUMBER && o.type != TokenType.STRING) {
            throw new IllegalArgumentException("Cannot compare tokens of type " + o.type);
        }

        if (o.type == TokenType.NUMBER) {
            return (new BigDecimal(lexeme)).compareTo(new BigDecimal(o.lexeme));
        } else {
            return (lexeme).compareTo(o.lexeme);
        }
    }
}
