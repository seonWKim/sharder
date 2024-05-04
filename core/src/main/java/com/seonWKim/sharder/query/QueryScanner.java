package com.seonWKim.sharder.query;

import static com.seonWKim.sharder.TokenType.BANG;
import static com.seonWKim.sharder.TokenType.COMMA;
import static com.seonWKim.sharder.TokenType.DOT;
import static com.seonWKim.sharder.TokenType.EQUAL;
import static com.seonWKim.sharder.TokenType.EQUAL_EQUAL;
import static com.seonWKim.sharder.TokenType.GREATER_THAN;
import static com.seonWKim.sharder.TokenType.GREATER_THAN_OR_EQUAL;
import static com.seonWKim.sharder.TokenType.LEFT_BRACE;
import static com.seonWKim.sharder.TokenType.LEFT_PAREN;
import static com.seonWKim.sharder.TokenType.LESS_THAN;
import static com.seonWKim.sharder.TokenType.LESS_THAN_OR_EQUAL;
import static com.seonWKim.sharder.TokenType.MINUS;
import static com.seonWKim.sharder.TokenType.NOT_EQUAL;
import static com.seonWKim.sharder.TokenType.PLUS;
import static com.seonWKim.sharder.TokenType.RIGHT_BRACE;
import static com.seonWKim.sharder.TokenType.RIGHT_PAREN;
import static com.seonWKim.sharder.TokenType.SEMICOLON;
import static com.seonWKim.sharder.TokenType.SLASH;
import static com.seonWKim.sharder.TokenType.STAR;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seonWKim.sharder.DefinitionType;
import com.seonWKim.sharder.Scanner;
import com.seonWKim.sharder.TokenType;

/**
 * Scans the query string and tokenizes it.<p>
 * This is a non thread-safe class. You have to create a new {@link QueryScanner} instance for each query.
 * Multiline queries are also supported.<p>
 * Examples<p>
 * 1) SELECT * FROM members;<p>
 * 2) SELECT id, age, databaseName FROM members;<p>
 * 3) SELECT * FROM test.members;<p>
 * 4) SELECT * FROM test.members WHERE (id = 10);
 */
public class QueryScanner extends Scanner {
    private static final Logger logger = LoggerFactory.getLogger(QueryScanner.class);

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("SELECT", TokenType.SELECT);
        keywords.put("INSERT", TokenType.INSERT);
        keywords.put("INTO", TokenType.INTO);
        keywords.put("VALUES", TokenType.VALUES);
        keywords.put("UPDATE", TokenType.UPDATE);
        keywords.put("SET", TokenType.SET);
        keywords.put("DELETE", TokenType.DELETE);
        keywords.put("FROM", TokenType.FROM);
        keywords.put("WHERE", TokenType.WHERE);
        keywords.put("AND", TokenType.AND);
        keywords.put("OR", TokenType.OR);
        keywords.put("ORDER BY", TokenType.ORDER_BY);
        keywords.put("ASC", TokenType.ASC);
        keywords.put("DESC", TokenType.DESC);
        keywords.put("LIMIT", TokenType.LIMIT);
        keywords.put("OFFSET", TokenType.OFFSET);
        keywords.put("GROUP", TokenType.GROUP);
        keywords.put("HAVING", TokenType.HAVING);
        keywords.put("JOIN", TokenType.JOIN);
        keywords.put("ON", TokenType.ON);
    }

    public QueryScanner(String query) {
        super(query);
    }

    @Override
    protected void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(matchAndAdvance('=') ? NOT_EQUAL : BANG);
                break;
            case '=':
                addToken(matchAndAdvance('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(matchAndAdvance('=') ? LESS_THAN_OR_EQUAL : LESS_THAN);
                break;
            case '>':
                addToken(matchAndAdvance('=') ? GREATER_THAN_OR_EQUAL : GREATER_THAN);
                break;
            case '/':
                if (matchAndAdvance('/')) {
                    while (peek() != '\n' && !isAtEnd()) {advance();}
                } else {
                    addToken(SLASH);
                }
                break;
            case '%':
                addToken(TokenType.MOD);
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"':
                doubleQuoteString();
                break;
            case '\'':
                singleQuoteString();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    logger.error("Unexpected character (" + c + ") at line " + line);
                }
                break;
        }
    }

    @Override
    protected boolean tokenTypeSupported(TokenType type) {
        return type.supports(DefinitionType.QUERY);
    }

    @Override
    protected Map<String, TokenType> keywords() {
        return keywords;
    }
}
