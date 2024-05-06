package io.github.seonwkim.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.seonwkim.Token;
import io.github.seonwkim.TokenType;

class QueryScannerTest {

    @Test
    void select_query_parsing_test() {
        String selectQuery = "SELECT * FROM members;";

        QueryScanner queryScanner = new QueryScanner(selectQuery);
        List<Token> result = queryScanner.scanTokens();
        Assertions.assertEquals(result.get(0).type(), TokenType.SELECT);
        assertEquals(result.get(1).type(), TokenType.STAR);
        assertEquals(result.get(2).type(), TokenType.FROM);
        assertEquals(result.get(3).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(3).literal(), "members");
        assertEquals(result.get(4).type(), TokenType.SEMICOLON);
        assertEquals(result.get(5).type(), TokenType.EOF);
    }

    @Test
    void select_where_query_parsing_test() {
        String selectQuery = "SELECT * FROM members WHERE databaseName = 'Alice';";

        QueryScanner queryScanner = new QueryScanner(selectQuery);
        List<Token> result = queryScanner.scanTokens();
        assertEquals(result.get(0).type(), TokenType.SELECT);
        assertEquals(result.get(1).type(), TokenType.STAR);
        assertEquals(result.get(2).type(), TokenType.FROM);
        assertEquals(result.get(3).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(3).literal(), "members");
        assertEquals(result.get(4).type(), TokenType.WHERE);
        assertEquals(result.get(5).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(6).type(), TokenType.EQUAL);
        assertEquals(result.get(7).type(), TokenType.STRING);
        assertEquals(result.get(7).literal(), "Alice");
        assertEquals(result.get(8).type(), TokenType.SEMICOLON);
    }

    @Test
    void select_range_where_parsing_test() {
        String selectQuery = "SELECT * FROM members WHERE id >= 0 AND id < 50;";

        QueryScanner queryScanner = new QueryScanner(selectQuery);
        List<Token> result = queryScanner.scanTokens();
        assertEquals(result.get(0).type(), TokenType.SELECT);
        assertEquals(result.get(1).type(), TokenType.STAR);
        assertEquals(result.get(2).type(), TokenType.FROM);
        assertEquals(result.get(3).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(3).literal(), "members");
        assertEquals(result.get(4).type(), TokenType.WHERE);
        assertEquals(result.get(5).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(5).literal(), "id");
        assertEquals(result.get(6).type(), TokenType.GREATER_THAN_OR_EQUAL);
        assertEquals(result.get(7).type(), TokenType.NUMBER);
        assertEquals(result.get(7).literal(), new BigDecimal("0"));
        assertEquals(result.get(8).type(), TokenType.AND);
        assertEquals(result.get(9).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(9).literal(), "id");
        assertEquals(result.get(10).type(), TokenType.LESS_THAN);
        assertEquals(result.get(11).type(), TokenType.NUMBER);
        assertEquals(result.get(11).literal(), new BigDecimal("50"));
        assertEquals(result.get(12).type(), TokenType.SEMICOLON);
    }

    @Test
    void insert_query_parsing_test() {
        String insertQuery = "INSERT INTO members (databaseName, age) VALUES ('Alice', 20);";

        QueryScanner queryScanner = new QueryScanner(insertQuery);
        List<Token> result = queryScanner.scanTokens();
        assertEquals(result.get(0).type(), TokenType.INSERT);
        assertEquals(result.get(1).type(), TokenType.INTO);
        assertEquals(result.get(2).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(2).literal(), "members");
        assertEquals(result.get(3).type(), TokenType.LEFT_PAREN);
        assertEquals(result.get(4).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(4).literal(), "databaseName");
        assertEquals(result.get(5).type(), TokenType.COMMA);
        assertEquals(result.get(6).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(6).literal(), "age");
        assertEquals(result.get(7).type(), TokenType.RIGHT_PAREN);
        assertEquals(result.get(8).type(), TokenType.VALUES);
        assertEquals(result.get(9).type(), TokenType.LEFT_PAREN);
        assertEquals(result.get(10).type(), TokenType.STRING);
        assertEquals(result.get(10).literal(), "Alice");
        assertEquals(result.get(11).type(), TokenType.COMMA);
        assertEquals(result.get(12).type(), TokenType.NUMBER);
        assertEquals(result.get(12).literal(), new BigDecimal("20"));
        assertEquals(result.get(13).type(), TokenType.RIGHT_PAREN);
        assertEquals(result.get(14).type(), TokenType.SEMICOLON);
    }

    @Test
    void update_query_parsing_test() {
        String updateQuery = "UPDATE members SET age = 21 WHERE databaseName = 'Alice';";

        QueryScanner queryScanner = new QueryScanner(updateQuery);
        List<Token> result = queryScanner.scanTokens();
        assertEquals(result.get(0).type(), TokenType.UPDATE);
        assertEquals(result.get(1).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(1).literal(), "members");
        assertEquals(result.get(2).type(), TokenType.SET);
        assertEquals(result.get(3).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(3).literal(), "age");
        assertEquals(result.get(4).type(), TokenType.EQUAL);
        assertEquals(result.get(5).type(), TokenType.NUMBER);
        assertEquals(result.get(5).literal(), new BigDecimal("21"));
        assertEquals(result.get(6).type(), TokenType.WHERE);
        assertEquals(result.get(7).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(7).literal(), "databaseName");
        assertEquals(result.get(8).type(), TokenType.EQUAL);
        assertEquals(result.get(9).type(), TokenType.STRING);
        assertEquals(result.get(9).literal(), "Alice");
        assertEquals(result.get(10).type(), TokenType.SEMICOLON);
    }

    @Test
    void delete_query_parsing_test() {
        String deleteQuery = "DELETE FROM members WHERE databaseName = 'Alice';";

        QueryScanner queryScanner = new QueryScanner(deleteQuery);
        List<Token> result = queryScanner.scanTokens();
        assertEquals(result.get(0).type(), TokenType.DELETE);
        assertEquals(result.get(1).type(), TokenType.FROM);
        assertEquals(result.get(2).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(2).literal(), "members");
        assertEquals(result.get(3).type(), TokenType.WHERE);
        assertEquals(result.get(4).type(), TokenType.IDENTIFIER);
        assertEquals(result.get(4).literal(), "databaseName");
        assertEquals(result.get(5).type(), TokenType.EQUAL);
        assertEquals(result.get(6).type(), TokenType.STRING);
        assertEquals(result.get(6).literal(), "Alice");
        assertEquals(result.get(7).type(), TokenType.SEMICOLON);
    }
}
