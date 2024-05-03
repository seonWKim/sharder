package com.sharder.query;

import java.util.ArrayList;
import java.util.List;

import com.sharder.Expression;
import com.sharder.ExpressionStatement;
import com.sharder.Parser;
import com.sharder.Statement;
import com.sharder.Token;
import com.sharder.TokenType;
import com.sharder.TokenTypeCategory;
import com.sharder.query.state.InsertStatement;
import com.sharder.query.state.SelectStatement;
import com.sharder.query.state.WhereStatement;
import com.sharder.query.state.expr.ConditionExpression;
import com.sharder.query.state.expr.SemicolonExpression;

/**
 * Parse query language. Supports SELECT, INSERT, UPDATE, DELETE statements including WHERE clause.<p>
 * Note that this is non thread safe. You have to create a {@link QueryParser} instance for each parsing
 * operation.<p>
 */
public class QueryParser extends Parser {

    public QueryParser(List<Token> tokens) {
        super(tokens);
    }

    @Override
    protected Statement statement() {
        if (match(TokenType.SELECT)) {
            return selectStatement();
        } else if (match(TokenType.INSERT)) {
            return insertStatement();
        }
        // TODO: insert, update, delete

        if (match(TokenType.WHERE)) {
            return whereStatement();
        }

        // consume left overs
        return ExpressionStatement.builder().expression(expression()).build();
    }

    /**
     * e.g. SELECT * FROM members WHERE databaseName = 'Alice';
     */
    private Statement selectStatement() {
        consumeAndAdvance(TokenType.SELECT, "Expect SELECT keyword");

        SelectStatement.SelectStatementBuilder builder = SelectStatement.builder();
        final boolean selectStar = peek().type() == TokenType.STAR;
        final List<String> fields = new ArrayList<>();
        if (selectStar) {
            builder.selectStar(true);
            advance();
        } else {
            while (matchWithAdvance(TokenType.IDENTIFIER)) {
                fields.add(previous().lexeme());

                if (!matchWithAdvance(TokenType.COMMA)) {
                    break;
                }
            }
        }
        builder.fields(fields);

        consumeAndAdvance(TokenType.FROM, "Expect FROM keyword");
        final Token schemaOrTable = consumeAndAdvance(TokenType.IDENTIFIER,
                                                      "Expect schema or table databaseName");
        builder.tableName(schemaOrTable.lexeme());

        if (match(TokenType.DOT)) {
            advance();
            final Token table = consumeAndAdvance(TokenType.IDENTIFIER, "Expect table databaseName");

            builder.schemaName(schemaOrTable.lexeme());
            builder.tableName(table.lexeme());
        }

        return builder.build();
    }

    /**
     * e.g. INSERT INTO person (id, name) VALUES (1, 'Alice');
     */
    private Statement insertStatement() {
        consumeAndAdvance(TokenType.INSERT, "Expect INSERT keyword");
        consumeAndAdvance(TokenType.INTO, "Expect INTO keyword");

        final InsertStatement.InsertStatementBuilder builder = InsertStatement.builder();
        final Token schemaOrTable = consumeAndAdvance(TokenType.IDENTIFIER,
                                                      "Expect schema or table databaseName");
        final String tableName = schemaOrTable.lexeme();
        if (match(TokenType.DOT)) {
            advance();
            final Token table = consumeAndAdvance(TokenType.IDENTIFIER, "Expect table databaseName");
            builder.schemaName(schemaOrTable.lexeme()).tableName(table.lexeme());
        } else {
            builder.tableName(tableName);
        }

        // e.g. (id, name)
        consumeAndAdvance(TokenType.LEFT_PAREN, "Expect opening parenthesis");
        final List<Token> columns = new ArrayList<>();
        while (matchWithAdvance(TokenType.IDENTIFIER)) {
            columns.add(previous());

            if (!matchWithAdvance(TokenType.COMMA)) {
                break;
            }
        }
        builder.columns(columns);
        consumeAndAdvance(TokenType.RIGHT_PAREN, "Expect closing parenthesis");

        // e.g. VALUES (1, 'Alice');
        consumeAndAdvance(TokenType.VALUES, "Expect VALUES keyword");
        consumeAndAdvance(TokenType.LEFT_PAREN, "Expect opening parenthesis");
        final List<Token> values = new ArrayList<>();
        while (matchWithAdvance(TokenType.NUMBER) || matchWithAdvance(TokenType.STRING)) {
            values.add(previous());

            if (!matchWithAdvance(TokenType.COMMA)) {
                break;
            }
        }
        consumeAndAdvance(TokenType.RIGHT_PAREN, "Expect closing parenthesis");

        builder.values(values);
        return builder.build();
    }

    /**
     * e.g WHERE databaseName = 'Alice';
     */
    private Statement whereStatement() {
        consumeAndAdvance(TokenType.WHERE, "Expect WHERE keyword");
        return WhereStatement.builder().expression(conditionExpression()).build();
    }

    private Expression expression() {
        if (match(TokenType.SEMICOLON)) {
            return semicolonExpression();
        }

        throw new ParseError("No more expression to parse");
    }

    private Expression semicolonExpression() {
        consumeAndAdvance(TokenType.SEMICOLON, "Expect semicolon");
        return SemicolonExpression.builder().build();
    }

    private Expression conditionExpression() {
        List<Token> tokens = new ArrayList<>();

        while (isCurrentPositionWhereClause()) {
            tokens.add(advance());
        }

        return new ConditionExpression(tokens);
    }

    private boolean isCurrentPositionWhereClause() {
        if (isAtEnd()) {
            return false;
        }

        final Token current = peek();
        if (current.type() == TokenType.EOF || current.type() == TokenType.SEMICOLON) {return false;}

        if (current.type().getCategory() == TokenTypeCategory.KEYWORD) {
            return current.type() == TokenType.AND || current.type() == TokenType.OR;
        }

        return true;
    }

}

