package com.sharder.query;

import static com.sharder.TokenType.AND;
import static com.sharder.TokenType.EQUAL;
import static com.sharder.TokenType.IDENTIFIER;
import static com.sharder.TokenType.LEFT_PAREN;
import static com.sharder.TokenType.NUMBER;
import static com.sharder.TokenType.RIGHT_PAREN;
import static com.sharder.TokenType.STRING;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.sharder.ExpressionStatement;
import com.sharder.Statement;
import com.sharder.Token;
import com.sharder.query.state.InsertStatement;
import com.sharder.query.state.SelectStatement;
import com.sharder.query.state.UpdateStatement;
import com.sharder.query.state.WhereStatement;
import com.sharder.query.state.expr.SemicolonExpression;
import com.sharder.query.state.expr.ConditionExpression;

class QueryParserTest {

    @Test
    void select_statement_test_select_by_star() {
        String selectQuery = "SELECT * FROM members;";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(SelectStatement.class);
        final SelectStatement selectStatement = (SelectStatement) statement.get(0);
        assertTrue(selectStatement.isSelectStar());
        assertThat(selectStatement.getFields()).isEmpty();
        assertThat(selectStatement.getTableName()).isEqualTo("members");

        assertThat(statement.get(1).getClass()).isEqualTo(ExpressionStatement.class);
        final ExpressionStatement expressionStatement = (ExpressionStatement) statement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(SemicolonExpression.class);
    }

    @Test
    void select_statement_test_select_by_fields() {
        String selectQuery = "SELECT id, age, databaseName FROM members";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();
        assertEquals(statement.get(0).getClass(), SelectStatement.class);

        final SelectStatement selectStatement = (SelectStatement) statement.get(0);
        assertFalse(selectStatement.isSelectStar());
        assertThat(selectStatement.getFields()).containsExactly("id", "age", "databaseName");
        assertThat(selectStatement.getSchemaName()).isNull();
        assertThat(selectStatement.getTableName()).isEqualTo("members");
    }

    @Test
    void select_statement_test_select_star_with_semicolon() {
        String selectQuery = "SELECT * FROM test.members;";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(SelectStatement.class);
        final SelectStatement selectStatement = (SelectStatement) statement.get(0);
        assertTrue(selectStatement.isSelectStar());
        assertThat(selectStatement.getFields()).isEmpty();
        assertThat(selectStatement.getSchemaName()).isEqualTo("test");
        assertThat(selectStatement.getTableName()).isEqualTo("members");

        assertThat(statement.get(1).getClass()).isEqualTo(ExpressionStatement.class);
        final ExpressionStatement expressionStatement = (ExpressionStatement) statement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(SemicolonExpression.class);
    }

    @Test
    void select_statement_test_where_single_condition() {
        String selectQuery = "SELECT * FROM test.members WHERE id = 10;";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(SelectStatement.class);
        final SelectStatement selectStatement = (SelectStatement) statement.get(0);
        assertTrue(selectStatement.isSelectStar());
        assertThat(selectStatement.getFields()).isEmpty();
        assertThat(selectStatement.getSchemaName()).isEqualTo("test");
        assertThat(selectStatement.getTableName()).isEqualTo("members");

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement expressionStatement = (WhereStatement) statement.get(1);
        assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression whereExpr = (ConditionExpression) expressionStatement.getExpression();
        assertThat(whereExpr.getTree().getTokens().size()).isEqualTo(3);
        assertThat(whereExpr.getTree().getTokens().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(whereExpr.getTree().getTokens().get(1).type()).isEqualTo(EQUAL);
        assertThat(whereExpr.getTree().getTokens().get(2).type()).isEqualTo(NUMBER);

        assertThat(statement.get(2).getClass()).isEqualTo(ExpressionStatement.class);
        final ExpressionStatement semicolonExpr = (ExpressionStatement) statement.get(2);
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

    @Test
    void insert_statement_test_table_name_only() {
        String insertQuery = "INSERT INTO members (id, age) VALUES (1, 20);";
        QueryScanner queryScanner = new QueryScanner(insertQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(InsertStatement.class);
        final InsertStatement insertStatement = (InsertStatement) statement.get(0);

        assertThat(insertStatement.getSchemaName()).isNull();
        assertThat(insertStatement.getTableName()).isEqualTo("members");

        assertThat(insertStatement.getColumns().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(insertStatement.getColumns().get(0).lexeme()).isEqualTo("id");
        assertThat(insertStatement.getValues().get(0).type()).isEqualTo(NUMBER);
        assertThat(insertStatement.getValues().get(0).lexeme()).isEqualTo("1");

        assertThat(insertStatement.getColumns().get(1).type()).isEqualTo(IDENTIFIER);
        assertThat(insertStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        assertThat(insertStatement.getValues().get(1).type()).isEqualTo(NUMBER);
        assertThat(insertStatement.getValues().get(1).lexeme()).isEqualTo("20");
    }

    @Test
    void insert_statement_test_with_schema_name() {
        String insertQuery = "INSERT INTO test.members (id, age) VALUES (1, 20);";
        QueryScanner queryScanner = new QueryScanner(insertQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(InsertStatement.class);
        final InsertStatement insertStatement = (InsertStatement) statement.get(0);

        assertThat(insertStatement.getSchemaName()).isEqualTo("test");
        assertThat(insertStatement.getTableName()).isEqualTo("members");

        assertThat(insertStatement.getColumns().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(insertStatement.getColumns().get(0).lexeme()).isEqualTo("id");
        assertThat(insertStatement.getValues().get(0).type()).isEqualTo(NUMBER);
        assertThat(insertStatement.getValues().get(0).lexeme()).isEqualTo("1");

        assertThat(insertStatement.getColumns().get(1).type()).isEqualTo(IDENTIFIER);
        assertThat(insertStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        assertThat(insertStatement.getValues().get(1).type()).isEqualTo(NUMBER);
        assertThat(insertStatement.getValues().get(1).lexeme()).isEqualTo("20");
    }

    @Test
    void insert_statement_with_invalid_number_of_columns_1() {
        String insertQuery = "INSERT INTO members (id, age) VALUES (1);";
        QueryScanner queryScanner = new QueryScanner(insertQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());

        assertThrows(IllegalArgumentException.class, queryParser::parse);
    }

    @Test
    void insert_statement_with_invalid_number_of_columns_2() {
        String insertQuery = "INSERT INTO members (id) VALUES (1, 20);";
        QueryScanner queryScanner = new QueryScanner(insertQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());

        assertThrows(IllegalArgumentException.class, queryParser::parse);
    }

    @Test
    void update_statement_test_table_name_ony() {
        String updateQuery = "UPDATE members SET name = 'Alice', age = 20;";
        QueryScanner queryScanner = new QueryScanner(updateQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(UpdateStatement.class);
        final UpdateStatement updateStatement = (UpdateStatement) statement.get(0);

        assertThat(updateStatement.getSchemaName()).isNull();
        assertThat(updateStatement.getTableName()).isEqualTo("members");

        assertThat(updateStatement.getColumns().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(updateStatement.getColumns().get(0).lexeme()).isEqualTo("name");
        assertThat(updateStatement.getValues().get(0).type()).isEqualTo(STRING);
        assertThat(updateStatement.getValues().get(0).lexeme()).isEqualTo("'Alice'");

        assertThat(updateStatement.getColumns().get(1).type()).isEqualTo(IDENTIFIER);
        assertThat(updateStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        assertThat(updateStatement.getValues().get(1).type()).isEqualTo(NUMBER);
        assertThat(updateStatement.getValues().get(1).lexeme()).isEqualTo("20");
    }

    @Test
    void update_statement_test_with_schema_name() {
        String updateQuery = "UPDATE test.members SET name = 'Alice', age = 20;";
        QueryScanner queryScanner = new QueryScanner(updateQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(UpdateStatement.class);
        final UpdateStatement updateStatement = (UpdateStatement) statement.get(0);
        assertThat(updateStatement.getSchemaName()).isEqualTo("test");
        assertThat(updateStatement.getTableName()).isEqualTo("members");

        assertThat(updateStatement.getColumns().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(updateStatement.getColumns().get(0).lexeme()).isEqualTo("name");
        assertThat(updateStatement.getValues().get(0).type()).isEqualTo(STRING);
        assertThat(updateStatement.getValues().get(0).lexeme()).isEqualTo("'Alice'");

        assertThat(updateStatement.getColumns().get(1).type()).isEqualTo(IDENTIFIER);
        assertThat(updateStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        assertThat(updateStatement.getValues().get(1).type()).isEqualTo(NUMBER);
        assertThat(updateStatement.getValues().get(1).lexeme()).isEqualTo("20");
    }

    @Test
    void update_statement_with_where_expression() {
        String updateQuery = "UPDATE members SET name = 'Alice', age = 20 WHERE id = 10;";
        QueryScanner queryScanner = new QueryScanner(updateQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(UpdateStatement.class);
        final UpdateStatement updateStatement = (UpdateStatement) statement.get(0);
        assertThat(updateStatement.getSchemaName()).isNull();
        assertThat(updateStatement.getTableName()).isEqualTo("members");

        assertThat(updateStatement.getColumns().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(updateStatement.getColumns().get(0).lexeme()).isEqualTo("name");
        assertThat(updateStatement.getValues().get(0).type()).isEqualTo(STRING);
        assertThat(updateStatement.getValues().get(0).lexeme()).isEqualTo("'Alice'");

        assertThat(updateStatement.getColumns().get(1).type()).isEqualTo(IDENTIFIER);
        assertThat(updateStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        assertThat(updateStatement.getValues().get(1).type()).isEqualTo(NUMBER);
        assertThat(updateStatement.getValues().get(1).lexeme()).isEqualTo("20");

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement whereStatement = (WhereStatement) statement.get(1);
        assertThat(whereStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) whereStatement.getExpression();
        assertThat(conditionExpression.getTree().getTokens().size()).isEqualTo(3);
        assertThat(conditionExpression.getTree().getTokens().get(0).type()).isEqualTo(IDENTIFIER);
        assertThat(conditionExpression.getTree().getTokens().get(1).type()).isEqualTo(EQUAL);
        assertThat(conditionExpression.getTree().getTokens().get(2).type()).isEqualTo(NUMBER);
    }
}
