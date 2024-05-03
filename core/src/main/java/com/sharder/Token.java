package com.sharder;

public record Token(TokenType type, String lexeme, Object literal, int line) {

    public static final int NOT_FROM_USER_INPUT = -1;

    public static final Token EQUAL = new Token(TokenType.EQUAL, "=", null, NOT_FROM_USER_INPUT);
    public static final Token OR = new Token(TokenType.OR, "OR", null, NOT_FROM_USER_INPUT);
    public static final Token AND = new Token(TokenType.AND, "AND", null, NOT_FROM_USER_INPUT);
}
