package io.github.seonWKim.query;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;

import io.github.seonWKim.ExpressionStatement;
import io.github.seonWKim.Statement;
import io.github.seonWKim.Token;
import io.github.seonWKim.TokenType;
import io.github.seonWKim.query.state.DeleteStatement;
import io.github.seonWKim.query.state.InsertStatement;
import io.github.seonWKim.query.state.SelectStatement;
import io.github.seonWKim.query.state.UpdateStatement;
import io.github.seonWKim.query.state.WhereStatement;
import io.github.seonWKim.query.state.expr.ConditionExpression;
import io.github.seonWKim.query.state.expr.SemicolonExpression;

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
        AssertionsForClassTypes.assertThat(expressionStatement.getExpression().getClass()).isEqualTo(
                ConditionExpression.class);
        final ConditionExpression whereExpr = (ConditionExpression) expressionStatement.getExpression();
        assertThat(whereExpr.getTree().getTokens().size()).isEqualTo(3);
        AssertionsForInterfaceTypes.assertThat(whereExpr.getTree().getTokens().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForInterfaceTypes.assertThat(whereExpr.getTree().getTokens().get(1).type()).isEqualTo(
                TokenType.EQUAL);
        AssertionsForInterfaceTypes.assertThat(whereExpr.getTree().getTokens().get(2).type()).isEqualTo(
                TokenType.NUMBER);

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

        AssertionsForClassTypes.assertThat(whereExpression.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression1 = (ConditionExpression) whereExpression.getExpression();
        assertThat(conditionExpression1.getTree().getTokens().size()).isEqualTo(5);
        AssertionsForInterfaceTypes.assertThat(conditionExpression1.getTree().getTokens().get(0).type()).isEqualTo(
                TokenType.LEFT_PAREN);
        AssertionsForInterfaceTypes.assertThat(conditionExpression1.getTree().getTokens().get(1).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForInterfaceTypes.assertThat(conditionExpression1.getTree().getTokens().get(2).type()).isEqualTo(
                TokenType.EQUAL);
        AssertionsForInterfaceTypes.assertThat(conditionExpression1.getTree().getTokens().get(3).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForInterfaceTypes.assertThat(conditionExpression1.getTree().getTokens().get(4).type()).isEqualTo(
                TokenType.RIGHT_PAREN);
    }

    @Test
    void select_statement_test_where_multiple_condition() {
        String selectQuery = "SELECT * FROM test.members WHERE id = 10 AND age = 20";
        QueryScanner queryScanner = new QueryScanner(selectQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement expressionStatement = (WhereStatement) statement.get(1);
        AssertionsForClassTypes.assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) expressionStatement.getExpression();

        final List<Token> whereExpressionTokens = conditionExpression.getTree().getTokens();
        assertThat(whereExpressionTokens.size()).isEqualTo(7);
        assertThat(whereExpressionTokens.get(0).type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(whereExpressionTokens.get(1).type()).isEqualTo(TokenType.EQUAL);
        assertThat(whereExpressionTokens.get(2).type()).isEqualTo(TokenType.NUMBER);
        assertThat(whereExpressionTokens.get(3).type()).isEqualTo(TokenType.AND);
        assertThat(whereExpressionTokens.get(4).type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(whereExpressionTokens.get(5).type()).isEqualTo(TokenType.EQUAL);
        assertThat(whereExpressionTokens.get(6).type()).isEqualTo(TokenType.NUMBER);
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
        AssertionsForClassTypes.assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) expressionStatement.getExpression();

        final List<Token> whereExpressionTokens = conditionExpression.getTree().getTokens();
        assertThat(whereExpressionTokens.size()).isEqualTo(7);
        assertThat(whereExpressionTokens.get(0).type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(whereExpressionTokens.get(1).type()).isEqualTo(TokenType.EQUAL);
        assertThat(whereExpressionTokens.get(2).type()).isEqualTo(TokenType.NUMBER);
        assertThat(whereExpressionTokens.get(3).type()).isEqualTo(TokenType.AND);
        assertThat(whereExpressionTokens.get(4).type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(whereExpressionTokens.get(5).type()).isEqualTo(TokenType.EQUAL);
        assertThat(whereExpressionTokens.get(6).type()).isEqualTo(TokenType.NUMBER);
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
        AssertionsForClassTypes.assertThat(expressionStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) expressionStatement.getExpression();

        final List<Token> whereExpressionTokens = conditionExpression.getTree().getTokens();
        assertThat(whereExpressionTokens.size()).isEqualTo(7);
        assertThat(whereExpressionTokens.get(0).type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(whereExpressionTokens.get(1).type()).isEqualTo(TokenType.EQUAL);
        assertThat(whereExpressionTokens.get(2).type()).isEqualTo(TokenType.NUMBER);
        assertThat(whereExpressionTokens.get(3).type()).isEqualTo(TokenType.AND);
        assertThat(whereExpressionTokens.get(4).type()).isEqualTo(TokenType.IDENTIFIER);
        assertThat(whereExpressionTokens.get(5).type()).isEqualTo(TokenType.EQUAL);
        assertThat(whereExpressionTokens.get(6).type()).isEqualTo(TokenType.NUMBER);
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

        AssertionsForInterfaceTypes.assertThat(insertStatement.getColumns().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(insertStatement.getColumns().get(0).lexeme()).isEqualTo("id");
        AssertionsForInterfaceTypes.assertThat(insertStatement.getValues().get(0).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForClassTypes.assertThat(insertStatement.getValues().get(0).lexeme()).isEqualTo("1");

        AssertionsForInterfaceTypes.assertThat(insertStatement.getColumns().get(1).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(insertStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        AssertionsForInterfaceTypes.assertThat(insertStatement.getValues().get(1).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForClassTypes.assertThat(insertStatement.getValues().get(1).lexeme()).isEqualTo("20");
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

        AssertionsForInterfaceTypes.assertThat(insertStatement.getColumns().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(insertStatement.getColumns().get(0).lexeme()).isEqualTo("id");
        AssertionsForInterfaceTypes.assertThat(insertStatement.getValues().get(0).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForClassTypes.assertThat(insertStatement.getValues().get(0).lexeme()).isEqualTo("1");

        AssertionsForInterfaceTypes.assertThat(insertStatement.getColumns().get(1).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(insertStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        AssertionsForInterfaceTypes.assertThat(insertStatement.getValues().get(1).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForClassTypes.assertThat(insertStatement.getValues().get(1).lexeme()).isEqualTo("20");
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

        AssertionsForInterfaceTypes.assertThat(updateStatement.getColumns().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(updateStatement.getColumns().get(0).lexeme()).isEqualTo("name");
        AssertionsForInterfaceTypes.assertThat(updateStatement.getValues().get(0).type()).isEqualTo(
                TokenType.STRING);
        AssertionsForClassTypes.assertThat(updateStatement.getValues().get(0).lexeme()).isEqualTo("'Alice'");

        AssertionsForInterfaceTypes.assertThat(updateStatement.getColumns().get(1).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(updateStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        AssertionsForInterfaceTypes.assertThat(updateStatement.getValues().get(1).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForClassTypes.assertThat(updateStatement.getValues().get(1).lexeme()).isEqualTo("20");
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

        AssertionsForInterfaceTypes.assertThat(updateStatement.getColumns().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(updateStatement.getColumns().get(0).lexeme()).isEqualTo("name");
        AssertionsForInterfaceTypes.assertThat(updateStatement.getValues().get(0).type()).isEqualTo(
                TokenType.STRING);
        AssertionsForClassTypes.assertThat(updateStatement.getValues().get(0).lexeme()).isEqualTo("'Alice'");

        AssertionsForInterfaceTypes.assertThat(updateStatement.getColumns().get(1).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(updateStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        AssertionsForInterfaceTypes.assertThat(updateStatement.getValues().get(1).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForClassTypes.assertThat(updateStatement.getValues().get(1).lexeme()).isEqualTo("20");
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

        AssertionsForInterfaceTypes.assertThat(updateStatement.getColumns().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(updateStatement.getColumns().get(0).lexeme()).isEqualTo("name");
        AssertionsForInterfaceTypes.assertThat(updateStatement.getValues().get(0).type()).isEqualTo(
                TokenType.STRING);
        AssertionsForClassTypes.assertThat(updateStatement.getValues().get(0).lexeme()).isEqualTo("'Alice'");

        AssertionsForInterfaceTypes.assertThat(updateStatement.getColumns().get(1).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForClassTypes.assertThat(updateStatement.getColumns().get(1).lexeme()).isEqualTo("age");
        AssertionsForInterfaceTypes.assertThat(updateStatement.getValues().get(1).type()).isEqualTo(
                TokenType.NUMBER);
        AssertionsForClassTypes.assertThat(updateStatement.getValues().get(1).lexeme()).isEqualTo("20");

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement whereStatement = (WhereStatement) statement.get(1);
        AssertionsForClassTypes.assertThat(whereStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) whereStatement.getExpression();
        assertThat(conditionExpression.getTree().getTokens().size()).isEqualTo(3);
        AssertionsForInterfaceTypes.assertThat(conditionExpression.getTree().getTokens().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForInterfaceTypes.assertThat(conditionExpression.getTree().getTokens().get(1).type()).isEqualTo(
                TokenType.EQUAL);
        AssertionsForInterfaceTypes.assertThat(conditionExpression.getTree().getTokens().get(2).type()).isEqualTo(
                TokenType.NUMBER);
    }

    @Test
    void delete_statement_test_table_name_only() {
        String deleteQuery = "DELETE FROM members;";
        QueryScanner queryScanner = new QueryScanner(deleteQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(DeleteStatement.class);
        final DeleteStatement deleteStatement = (DeleteStatement) statement.get(0);

        assertThat(deleteStatement.getSchemaName()).isNull();
        assertThat(deleteStatement.getTableName()).isEqualTo("members");
    }

    @Test
    void delete_statement_test_with_schema_name() {
        String deleteQuery = "DELETE FROM test.members;";
        QueryScanner queryScanner = new QueryScanner(deleteQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(DeleteStatement.class);
        final DeleteStatement deleteStatement = (DeleteStatement) statement.get(0);

        assertThat(deleteStatement.getSchemaName()).isEqualTo("test");
        assertThat(deleteStatement.getTableName()).isEqualTo("members");
    }

    @Test
    void delete_statement_with_where_expression() {
        String deleteQuery = "DELETE FROM members WHERE id = 10;";
        QueryScanner queryScanner = new QueryScanner(deleteQuery);
        QueryParser queryParser = new QueryParser(queryScanner.scanTokens());
        List<Statement> statement = queryParser.parse();

        assertThat(statement.get(0).getClass()).isEqualTo(DeleteStatement.class);
        final DeleteStatement deleteStatement = (DeleteStatement) statement.get(0);
        assertThat(deleteStatement.getSchemaName()).isNull();
        assertThat(deleteStatement.getTableName()).isEqualTo("members");

        assertThat(statement.get(1).getClass()).isEqualTo(WhereStatement.class);
        final WhereStatement whereStatement = (WhereStatement) statement.get(1);
        AssertionsForClassTypes.assertThat(whereStatement.getExpression().getClass()).isEqualTo(ConditionExpression.class);
        final ConditionExpression conditionExpression = (ConditionExpression) whereStatement.getExpression();
        assertThat(conditionExpression.getTree().getTokens().size()).isEqualTo(3);
        AssertionsForInterfaceTypes.assertThat(conditionExpression.getTree().getTokens().get(0).type()).isEqualTo(
                TokenType.IDENTIFIER);
        AssertionsForInterfaceTypes.assertThat(conditionExpression.getTree().getTokens().get(1).type()).isEqualTo(
                TokenType.EQUAL);
        AssertionsForInterfaceTypes.assertThat(conditionExpression.getTree().getTokens().get(2).type()).isEqualTo(
                TokenType.NUMBER);
    }
}
