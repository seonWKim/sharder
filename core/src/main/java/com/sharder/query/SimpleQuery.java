package com.sharder.query;

import static com.sharder.QueryType.DELETE;
import static com.sharder.QueryType.INSERT;
import static com.sharder.QueryType.SELECT;

import java.util.List;

import com.sharder.Expression;
import com.sharder.FirstStatement;
import com.sharder.Nullable;
import com.sharder.QueryType;
import com.sharder.Statement;
import com.sharder.StatementType;
import com.sharder.Token;
import com.sharder.query.state.InsertStatement;
import com.sharder.query.state.WhereStatement;

public class SimpleQuery {

    private final String queryString;
    private final QueryType queryType;
    private final FirstStatement firstStatement;
    @Nullable
    private final Expression conditionExpression;

    public static SimpleQuery of(String queryString) {
        return new SimpleQuery(queryString);
    }

    private SimpleQuery(String queryString) {
        this.queryString = queryString;

        List<Token> tokens = new QueryScanner(queryString).scanTokens();
        List<Statement> statements = new QueryParser(tokens).parse();
        if (statements.isEmpty()) {
            throw new IllegalStateException("No statement found in the query");
        }

        firstStatement = (FirstStatement) statements.get(0);
        final StatementType type = firstStatement.getStatementType();
        switch (type) {
            case QUERY_SELECT:
                this.queryType = SELECT;
                this.conditionExpression = getConditionExpression(statements);
                break;
            case QUERY_INSERT:
                this.queryType = INSERT;
                this.conditionExpression = ((InsertStatement) statements.get(0)).getConditionExpression();
                break;
            case QUERY_UPDATE:
                this.queryType = QueryType.UPDATE;
                this.conditionExpression = getConditionExpression(statements);
                break;
            case QUERY_DELETE:
                this.queryType = DELETE;
                this.conditionExpression = getConditionExpression(statements);
                break;
            default:
                throw new IllegalStateException("Unsupported query type: " + type);
        }
    }

    public String tableName() {
        return firstStatement.tableName();
    }

    @Nullable
    private Expression getConditionExpression(List<Statement> statements) {
        List<Statement> whereStatements = statements.stream().filter(
                it -> it.getStatementType() == StatementType.QUERY_WHERE).toList();
        if (whereStatements.size() > 2) {
            throw new IllegalStateException("Too many where statements found in the query");
        } else if (whereStatements.size() == 1) {
            return ((WhereStatement) whereStatements.get(0)).getExpression();
        } else {
            return null;
        }
    }

    @Nullable
    public Expression conditionExpression() {
        return conditionExpression;
    }
}
