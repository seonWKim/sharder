package com.sharder;

public record Token(TokenType type, String lexeme, Object literal, int line) {}
