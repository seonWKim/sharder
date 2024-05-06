package io.github.seonwkim.query;

import java.util.ArrayList;
import java.util.List;

import io.github.seonwkim.Expression;
import io.github.seonwkim.ExpressionStatement;
import io.github.seonwkim.Parser;
import io.github.seonwkim.TokenTypeCategory;
import io.github.seonwkim.query.state.InsertStatement;
import io.github.seonwkim.query.state.InsertStatement.InsertStatementBuilder;
import io.github.seonwkim.query.state.SelectStatement;
import io.github.seonwkim.query.state.SelectStatement.SelectStatementBuilder;
import io.github.seonwkim.Statement;
import io.github.seonwkim.Token;
import io.github.seonwkim.TokenType;
import io.github.seonwkim.query.state.DeleteStatement;
import io.github.seonwkim.query.state.UpdateStatement;
import io.github.seonwkim.query.state.WhereStatement;
import io.github.seonwkim.query.state.expr.ConditionExpression;
import io.github.seonwkim.query.state.expr.SemicolonExpression;

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
        Token current = peek();
        return switch (current.type()) {
            case SELECT -> selectStatement();
            case INSERT -> insertStatement();
            case UPDATE -> updateStatement();
            case DELETE -> deleteStatement();
            case WHERE -> whereStatement();
            default -> ExpressionStatement.builder().expression(expression()).build();
        };
    }

    /**
     * e.g. SELECT * FROM members;
     */
    private Statement selectStatement() {
        consumeAndAdvance(TokenType.SELECT, "Expect SELECT keyword");

        SelectStatementBuilder builder = SelectStatement.builder();
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

        final InsertStatementBuilder builder = InsertStatement.builder();
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
     * e.g. UPDATE members SET databaseName = 'Alice', age = 20
     */
    private Statement updateStatement() {
        consumeAndAdvance(TokenType.UPDATE, "Expect UPDATE keyword");

        final UpdateStatement.UpdateStatementBuilder builder = UpdateStatement.builder();
        final Token schemaOrTable =
                consumeAndAdvance(TokenType.IDENTIFIER, "Expect schema or table databaseName");
        final String tableName = schemaOrTable.lexeme();
        if (match(TokenType.DOT)) {
            advance();
            final Token table = consumeAndAdvance(TokenType.IDENTIFIER, "Expect table databaseName");
            builder.schemaName(schemaOrTable.lexeme()).tableName(table.lexeme());
        } else {
            builder.tableName(tableName);
        }

        consumeAndAdvance(TokenType.SET, "Expect SET keyword");

        final List<Token> columns = new ArrayList<>();
        final List<Token> values = new ArrayList<>();
        while (matchWithAdvance(TokenType.IDENTIFIER)) {
            columns.add(previous());
            consumeAndAdvance(TokenType.EQUAL, "Expect equal sign");
            values.add(advance());

            if (!matchWithAdvance(TokenType.COMMA)) {
                break;
            }
        }
        builder.columns(columns).values(values);

        return builder.build();
    }

    /**
     * e.g. DELETE FROM members
     */
    private Statement deleteStatement() {
        consumeAndAdvance(TokenType.DELETE, "Expect DELETE keyword");
        consumeAndAdvance(TokenType.FROM, "Expect FROM keyword");

        final DeleteStatement.DeleteStatementBuilder builder = DeleteStatement.builder();
        final Token schemaOrTable = consumeAndAdvance(TokenType.IDENTIFIER, "Expect schema or table databaseName");
        final String tableName = schemaOrTable.lexeme();

        if (match(TokenType.DOT)) {
            advance();
            final Token table = consumeAndAdvance(TokenType.IDENTIFIER, "Expect table databaseName");
            builder.schemaName(schemaOrTable.lexeme()).tableName(table.lexeme());
        } else {
            builder.tableName(tableName);
        }

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

