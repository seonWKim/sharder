package com.sharder.query;

import static com.sharder.QueryType.DELETE;
import static com.sharder.QueryType.INSERT;
import static com.sharder.QueryType.SELECT;

import java.util.List;

import com.sharder.Expression;
import com.sharder.QueryType;
import com.sharder.Statement;
import com.sharder.StatementType;
import com.sharder.Token;
import com.sharder.query.state.WhereStatement;

import com.sharder.Nullable;
public class SimpleQuery {

    private final String queryString;
    private final List<Statement> statements;
    private final QueryType queryType;
    @Nullable
    private final WhereStatement whereStatement;

    private SimpleQuery(String queryString) {
        this.queryString = queryString;

        List<Token> tokens = new QueryScanner(queryString).scanTokens();
        this.statements = new QueryParser(tokens).parse();
        if (statements.isEmpty()) {
            throw new IllegalStateException("No statement found in the query");
        }

        final StatementType type = statements.get(0).getStatementType();
        switch (type) {
            case QUERY_SELECT:
                this.queryType = SELECT;
                break;
            case QUERY_INSERT:
                this.queryType = INSERT;
                break;
            case QUERY_UPDATE:
                this.queryType = QueryType.UPDATE;
                break;
            case QUERY_DELETE:
                this.queryType = DELETE;
                break;
            default:
                throw new IllegalStateException("Unsupported query type: " + type);
        }

        List<Statement> whereStatements = statements.stream().filter(
                it -> it.getStatementType() == StatementType.QUERY_WHERE).toList();
        if (whereStatements.size() > 2) {
            throw new IllegalStateException("Too many where statements found in the query");
        } else if (whereStatements.size() == 1) {
            this.whereStatement = (WhereStatement) whereStatements.get(0);
        } else {
            this.whereStatement = null;
        }
    }

    public static SimpleQuery of(String queryString) {
        return new SimpleQuery(queryString);
    }

    public boolean hasWhereStatement() {
        return whereStatement != null;
    }

    @Nullable
    public Expression condition() {
        if (whereStatement == null) {
            return null;
        }

        return whereStatement.getExpression();
    }
}
