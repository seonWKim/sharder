package io.github.seonwkim;

import java.util.List;

import io.github.seonwkim.query.QueryParser;
import io.github.seonwkim.query.QueryScanner;
import io.github.seonwkim.query.state.InsertStatement;
import io.github.seonwkim.query.state.WhereStatement;

/**
 * Represents a single query. Supports SELECT, INSERT, UPDATE, DELETE queries.
 */
public class SimpleQuery {

    private final StatementWithTableMeta statementWithTableMeta;
    @Nullable
    private final Expression conditionExpression;

    public static SimpleQuery of(String queryString) {
        return new SimpleQuery(queryString);
    }

    private SimpleQuery(String queryString) {
        List<Token> tokens = new QueryScanner(queryString).scanTokens();
        List<Statement> statements = new QueryParser(tokens).parse();
        if (statements.isEmpty()) {
            throw new IllegalStateException("No statement found in the query");
        }

        statementWithTableMeta = (StatementWithTableMeta) statements.get(0);
        final StatementType type = statementWithTableMeta.getStatementType();
        switch (type) {
            case QUERY_SELECT, QUERY_UPDATE, QUERY_DELETE:
                this.conditionExpression = getConditionExpression(statements);
                break;
            case QUERY_INSERT:
                this.conditionExpression = ((InsertStatement) statements.get(0)).getConditionExpression();
                break;
            default:
                throw new IllegalStateException("Unsupported query type: " + type);
        }
    }

    public String tableName() {
        return statementWithTableMeta.tableName();
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
