package io.github.seonwkim.shard;

import static io.github.seonwkim.TokenType.BANG;
import static io.github.seonwkim.TokenType.DOT;
import static io.github.seonwkim.TokenType.EQUAL;
import static io.github.seonwkim.TokenType.EQUAL_EQUAL;
import static io.github.seonwkim.TokenType.GREATER_THAN;
import static io.github.seonwkim.TokenType.GREATER_THAN_OR_EQUAL;
import static io.github.seonwkim.TokenType.LESS_THAN;
import static io.github.seonwkim.TokenType.LESS_THAN_OR_EQUAL;
import static io.github.seonwkim.TokenType.NOT_EQUAL;
import static io.github.seonwkim.TokenType.SLASH;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.seonwkim.DefinitionType;
import io.github.seonwkim.Scanner;
import io.github.seonwkim.TokenType;

/**
 * Scans the shard definition string and tokenizes it.<p>
 * This is a non thread-safe class. You have to create a new {@link ShardDefinitionScanner} instance for each shard definition.
 * Multiline shard definitions are also supported.<p>
 * Examples<p>
 * 1) members.id % 2 == 0
 * 2) members.id > 0 AND members.id < 10
 */
public class ShardDefinitionScanner extends Scanner {

    private static final Logger logger = LoggerFactory.getLogger(ShardDefinitionScanner.class);

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("AND", TokenType.AND);
        keywords.put("OR", TokenType.OR);
    }

    public ShardDefinitionScanner(String definition) {
        super(definition);
    }

    @Override
    protected void scanToken() {
        char c = advance();
        switch (c) {
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
            case '.':
                addToken(DOT);
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
        return type.supports(DefinitionType.SHARD);
    }

    @Override
    protected Map<String, TokenType> keywords() {
        return keywords;
    }
}
