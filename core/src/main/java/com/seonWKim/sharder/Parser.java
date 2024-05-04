package com.seonWKim.sharder;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Base class for parsing tokens into statements. <br>
 * Note that this class is not thread-safe.
 */
@RequiredArgsConstructor
public abstract class Parser {
    protected final List<Token> tokens;
    protected int current = 0;

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(statement());
        }

        return statements;
    }

    protected abstract Statement statement();

    protected boolean match(TokenType... types) {
        int pos = current;
        for (TokenType type : types) {
            if (!check(type, pos)) {
                return false;
            }
            pos++;
        }

        return true;
    }

    /**
     * Checks whether the current tokens match with the given types. Advance {@link current} when matched.
     */
    protected boolean matchWithAdvance(TokenType... types) {
        for (TokenType type : types) {
            if (!check(type)) {
                return false;
            }
            advance();
        }

        return true;
    }

    protected Token consumeAndAdvance(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw new ParseError(message);
    }

    protected boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }

        return peek().type() == type;
    }

    protected boolean check(TokenType type, int idx) {
        if (isAtEnd(idx)) {
            return false;
        }

        return peek(idx).type() == type;
    }

    protected Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    protected Token previous() {
        return tokens.get(current - 1);
    }

    protected boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    protected boolean isAtEnd(int idx) {
        return peek(idx).type() == TokenType.EOF;
    }

    protected Token peek() {
        return tokens.get(current);
    }

    protected Token peek(int idx) {
        if (idx >= tokens.size()) {
            throw new ParseError("Index out of bound");
        }
        return tokens.get(idx);
    }

    protected static class ParseError extends RuntimeException {

        public ParseError(String msg) {
            super(msg);
        }
    }
}
