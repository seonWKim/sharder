package com.sharder;

import java.util.List;

import lombok.Getter;

@Getter
public enum TokenType {
    // Separators
    LEFT_PAREN(TokenTypeCategory.SEPARATOR, List.of(DefinitionType.QUERY)),
    RIGHT_PAREN(TokenTypeCategory.SEPARATOR, List.of(DefinitionType.QUERY)),
    LEFT_BRACE(TokenTypeCategory.SEPARATOR, List.of(DefinitionType.QUERY)),
    RIGHT_BRACE(TokenTypeCategory.SEPARATOR, List.of(DefinitionType.QUERY)),
    COMMA(TokenTypeCategory.SEPARATOR, List.of(DefinitionType.QUERY)),
    DOT(TokenTypeCategory.SEPARATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    SEMICOLON(TokenTypeCategory.SEPARATOR, List.of(DefinitionType.QUERY)),

    // Operators
    MINUS(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY)),
    PLUS(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY)),
    SLASH(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY)),
    MOD(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    STAR(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY)),
    BANG(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    NOT_EQUAL(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    EQUAL_EQUAL(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY)),
    EQUAL(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    GREATER_THAN(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    GREATER_THAN_OR_EQUAL(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    LESS_THAN(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    LESS_THAN_OR_EQUAL(TokenTypeCategory.OPERATOR, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),

    // Literals
    IDENTIFIER(TokenTypeCategory.LITERAL, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    STRING(TokenTypeCategory.LITERAL, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),
    NUMBER(TokenTypeCategory.LITERAL, List.of(DefinitionType.QUERY, DefinitionType.SHARD)),

    // Query Keywords
    SELECT(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    INSERT(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    INTO(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    UPDATE(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    SET(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    VALUES(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    DELETE(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    FROM(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    WHERE(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    AND(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    OR(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    ORDER_BY(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    ASC(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    DESC(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    LIMIT(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    OFFSET(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    GROUP(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    HAVING(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    JOIN(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),
    ON(TokenTypeCategory.KEYWORD, List.of(DefinitionType.QUERY)),

    // Shard definition keywords

    // EOF
    EOF(TokenTypeCategory.ETC, List.of(DefinitionType.QUERY, DefinitionType.SHARD));

    private final TokenTypeCategory category;
    private final int mask;

    TokenType(TokenTypeCategory category, List<DefinitionType> types) {
        this.category = category;
        this.mask = mask(types);
    }

    private int mask(List<DefinitionType> types) {
        return types.stream()
                    .mapToInt(DefinitionType::getMask)
                    .reduce(0, (a, b) -> a | b);
    }

    public boolean supports(DefinitionType type) {
        return (type.getMask() & this.mask) != 0;
    }
}
