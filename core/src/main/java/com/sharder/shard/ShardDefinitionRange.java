package com.sharder.shard;

import java.util.List;
import java.util.Set;

import com.sharder.Expression;
import com.sharder.Token;
import com.sharder.TokenType;

import lombok.Getter;

/**
 * table_name.column_name (< | <= | > | >=) value
 */
@Getter
public class ShardDefinitionRange implements ShardDefinition {
    private final String definitionStr;
    private final Token table;
    private final Token column;
    private final Token operator;
    private final Token value;

    // e.g. table_name.column_name < 10
    private final List<Set<TokenType>> validator = List.of(
            Set.of(TokenType.IDENTIFIER), Set.of(TokenType.DOT), Set.of(TokenType.IDENTIFIER),
            Set.of(TokenType.GREATER_THAN, TokenType.GREATER_THAN_OR_EQUAL, TokenType.LESS_THAN,
                   TokenType.LESS_THAN_OR_EQUAL),
            Set.of(TokenType.NUMBER, TokenType.STRING),
            Set.of(TokenType.EOF));

    public ShardDefinitionRange(String definitionStr) {
        this.definitionStr = definitionStr;
        List<Token> tokens = new ShardDefinitionScanner(definitionStr).scanTokens();

        if (validate(tokens, validator)) {
            this.table = tokens.get(0);
            this.column = tokens.get(2);
            this.operator = tokens.get(3);
            this.value = tokens.get(4);
        } else {
            throw new IllegalArgumentException("Invalid shard definition: " + definitionStr);
        }
    }

    @Override
    public ShardDefinitionType type() {
        return ShardDefinitionType.RANGE;
    }

    @Override
    public String tableName() {
        return table.lexeme();
    }

    @Override
    public String columnName() {
        return column.lexeme();
    }

    @Override
    public boolean match(Expression conditionExpression) {
        return false;
    }
}
