package io.github.seonWKim;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for scanning the query string and tokenizing it.<br>
 * Note that this class is not thread-safe.
 */
public abstract class Scanner {
    private final Logger logger = LoggerFactory.getLogger(Scanner.class);

    protected final String input;
    protected final List<Token> tokens = new ArrayList<>();

    protected int start = 0;
    protected int current = 0;
    protected int line = 1;

    public Scanner(String input) {
        this.input = input;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    protected abstract void scanToken();

    protected abstract boolean tokenTypeSupported(TokenType type);

    protected abstract Map<String, TokenType> keywords();

    protected boolean isAtEnd() {
        return current >= input.length();
    }

    protected boolean matchAndAdvance(char expected) {
        if (isAtEnd()) {return false;}
        if (input.charAt(current) != expected) {return false;}

        current++;
        return true;
    }

    protected char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return input.charAt(current);
    }

    protected char peekNext() {
        if (current + 1 >= input.length()) {
            return '\0';
        }
        return input.charAt(current + 1);
    }

    protected boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    protected boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    protected boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    protected char advance() {
        return input.charAt(current++);
    }

    protected void addToken(TokenType type) {
        if (!tokenTypeSupported(type)) {
            logger.error("Unsupported token type: " + type);
            return;
        }
        addToken(type, null);
    }

    protected void addToken(TokenType type, Object literal) {
        String text = input.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    protected String toUppercase(String text) {
        if (text == null) {
            return null;
        }
        return text.toUpperCase();
    }

    protected void number() {
        while (isDigit(peek())) {
            advance();
        }

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) {advance();}
        }

        addToken(TokenType.NUMBER, new BigDecimal(input.substring(start, current)));
    }

    protected void doubleQuoteString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {line++;}
            advance();
        }

        if (isAtEnd()) {
            logger.error("Unterminated string at line " + line);
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = input.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    protected void singleQuoteString() {
        while (peek() != '\'' && !isAtEnd()) {
            if (peek() == '\n') {line++;}
            advance();
        }

        if (isAtEnd()) {
            logger.error("Unterminated string at line " + line);
            return;
        }

        // The closing '.
        advance();

        // Trim the surrounding quotes.
        String value = input.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    protected void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = input.substring(start, current);
        TokenType type = keywords().get(toUppercase(text));
        if (type == null) {type = TokenType.IDENTIFIER;}

        addToken(type, text);
    }
}
