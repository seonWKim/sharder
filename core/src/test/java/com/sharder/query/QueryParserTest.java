package com.sharder.query;

import static com.sharder.TokenType.AND;
import static com.sharder.TokenType.EQUAL;
import static com.sharder.TokenType.IDENTIFIER;
import static com.sharder.TokenType.LEFT_PAREN;
import static com.sharder.TokenType.NUMBER;
import static com.sharder.TokenType.RIGHT_PAREN;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.sharder.ExpressionStatement;
import com.sharder.Statement;
import com.sharder.Token;
import com.sharder.query.state.SelectStatement;
import com.sharder.query.state.WhereStatement;
import com.sharder.query.state.expr.SemicolonExpression;
import com.sharder.query.state.expr.ConditionExpression;

class QueryParserTest {

    @Test
    void select_statement_test_select_by_star() {
        String selectQuery = "SELECT * FROM members;";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> queryStatement = queryParser.parse();

        assertThat(queryStatement.get(0).getClass()).isEqualTo(SelectStatement.class);
        final SelectStatement statement = (SelectStatement) queryStatement.get(0);
        assertTrue(statement.isSelectStar());
        assertThat(statement.getFields()).isEmpty();
        assertThat(statement.getTableName()).isEqualTo("members");

        assertThat(queryStatement.get(1).getClass()).isEqualTo(ExpressionStatement.class);
        final ExpressionStatement expressionStatement = (ExpressionStatement) queryStatement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(SemicolonExpression.class);
    }

    @Test
    void select_statement_test_select_by_fields() {
        String selectQuery = "SELECT id, age, databaseName FROM members";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> queryStatement = queryParser.parse();
        assertEquals(queryStatement.get(0).getClass(), SelectStatement.class);

        final SelectStatement statement = (SelectStatement) queryStatement.get(0);
        assertFalse(statement.isSelectStar());
        assertThat(statement.getFields()).containsExactly("id", "age", "databaseName");
        assertThat(statement.getSchemaName()).isNull();
        assertThat(statement.getTableName()).isEqualTo("members");
    }

    @Test
    void select_statement_test_select_star_with_semicolon() {
        String selectQuery = "SELECT * FROM test.members;";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> queryStatement = queryParser.parse();

        assertThat(queryStatement.get(0).getClass()).isEqualTo(SelectStatement.class);
        final SelectStatement statement = (SelectStatement) queryStatement.get(0);
        assertTrue(statement.isSelectStar());
        assertThat(statement.getFields()).isEmpty();
        assertThat(statement.getSchemaName()).isEqualTo("test");
        assertThat(statement.getTableName()).isEqualTo("members");

        assertThat(queryStatement.get(1).getClass()).isEqualTo(ExpressionStatement.class);
        final ExpressionStatement expressionStatement = (ExpressionStatement) queryStatement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(SemicolonExpression.class);
    }

    @Test
    void select_statement_test_where_single_condition() {
        String selectQuery = "SELECT * FROM test.members WHERE id = 10;";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> queryStatement = queryParser.parse();

        assertThat(queryStatement.get(0).getClass()).isEqualTo(SelectStatement.class);
        final SelectStatement statement = (SelectStatement) queryStatement.get(0);
        assertTrue(statement.isSelectStar());
        assertThat(statement.getFields()).isEmpty();
        assertThat(statement.getSchemaName()).isEqualTo("test");
        assertThat(statement.getTableName()).isEqualTo("members");

        assertThat(queryStatement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement expressionStatement = (WhereStatement) queryStatement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression whereExpr = (ConditionExpression) expressionStatement.getExpression();
        assertThat(whereExpr.getTree().getTokens().size()).isEqualTo(3);
        assertThat(whereExpr.getTree().getTokens().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpr.getTree().getTokens().get(1).type()).isEqualTo(EQUAL);
        assertThat(whereExpr.getTree().getTokens().get(2).type()).isEqualTo(NUMBER);

        assertThat(queryStatement.get(2).getClass()).isEqualTo(ExpressionStatement.class);
        final ExpressionStatement semicolonExpr = (ExpressionStatement) queryStatement.get(2);
        assertThat(semicolonExpr.getExpression().getClass()).isEqualTo(SemicolonExpression.class);
    }

    @Test
    void select_statement_test_where_parenthesis() {
        String selectQuery = "SELECT * FROM test.members WHERE (id = 10);";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement whereExpression = (WhereStatement) statement.get(1);

        assertThat(whereExpression.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression1 = (ConditionExpression) whereExpression.getExpression();
        assertThat(conditionExpression1.getTree().getTokens().size()).isEqualTo(5);
        assertThat(conditionExpression1.getTree().getTokens().get(0).type()).isEqualTo(LEFT_PAREN);
        assertThat(conditionExpression1.getTree().getTokens().get(1).type()).isEqualTo(IDENTIFIER);
        assertThat(conditionExpression1.getTree().getTokens().get(2).type()).isEqualTo(EQUAL);
        assertThat(conditionExpression1.getTree().getTokens().get(3).type()).isEqualTo(NUMBER);
        assertThat(conditionExpression1.getTree().getTokens().get(4).type()).isEqualTo(RIGHT_PAREN);
    }

    @Test
    void select_statement_test_where_multiple_condition() {
        String selectQuery = "SELECT * FROM test.members WHERE id = 10 AND age = 20";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement expressionStatement = (WhereStatement) statement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) expressionStatement.getExpression();

        final List<Token> whereExpressionTokens = conditionExpression.getTree().getTokens();
        assertThat(whereExpressionTokens.size()).isEqualTo(7);
        assertThat(whereExpressionTokens.get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpressionTokens.get(1).type()).isEqualTo(EQUAL);
        assertThat(whereExpressionTokens.get(2).type()).isEqualTo(NUMBER);
        assertThat(whereExpressionTokens.get(3).type()).isEqualTo(AND);
        assertThat(whereExpressionTokens.get(4).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpressionTokens.get(5).type()).isEqualTo(EQUAL);
        assertThat(whereExpressionTokens.get(6).type()).isEqualTo(NUMBER);
    }

    @Test
    void select_statement_test_where_multiple_condition_multi_line_1() {
        String selectQuery = """
                SELECT *
                FROM test.members
                WHERE id = 10 AND age = 20;
                """;
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement expressionStatement = (WhereStatement) statement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) expressionStatement.getExpression();

        final List<Token> whereExpressionTokens = conditionExpression.getTree().getTokens();
        assertThat(whereExpressionTokens.size()).isEqualTo(7);
        assertThat(whereExpressionTokens.get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpressionTokens.get(1).type()).isEqualTo(EQUAL);
        assertThat(whereExpressionTokens.get(2).type()).isEqualTo(NUMBER);
        assertThat(whereExpressionTokens.get(3).type()).isEqualTo(AND);
        assertThat(whereExpressionTokens.get(4).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpressionTokens.get(5).type()).isEqualTo(EQUAL);
        assertThat(whereExpressionTokens.get(6).type()).isEqualTo(NUMBER);
    }

    @Test
    void select_statement_test_where_multiple_condition_multi_line_2() {
        String selectQuery = """
                SELECT *
                FROM test.members
                WHERE id = 10
                AND age = 20;
                """;
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(SelectStatement.class);
        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement expressionStatement = (WhereStatement) statement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) expressionStatement.getExpression();

        final List<Token> whereExpressionTokens = conditionExpression.getTree().getTokens();
        assertThat(whereExpressionTokens.size()).isEqualTo(7);
        assertThat(whereExpressionTokens.get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpressionTokens.get(1).type()).isEqualTo(EQUAL);
        assertThat(whereExpressionTokens.get(2).type()).isEqualTo(NUMBER);
        assertThat(whereExpressionTokens.get(3).type()).isEqualTo(AND);
        assertThat(whereExpressionTokens.get(4).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpressionTokens.get(5).type()).isEqualTo(EQUAL);
        assertThat(whereExpressionTokens.get(6).type()).isEqualTo(NUMBER);
    }
}
