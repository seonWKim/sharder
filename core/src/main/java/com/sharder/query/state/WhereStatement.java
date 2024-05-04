package com.sharder.query.state;

import com.sharder.Expression;
import com.sharder.Statement;
import com.sharder.StatementType;

import lombok.Getter;

/**
 * Represents a WHERE statement in SQL.
 * e.g. WHERE column_name = value;
 */
@Getter
public class WhereStatement extends Statement {
    public static WhereStatementBuilder builder() {
        return new WhereStatementBuilder();
    }

    private final Expression expression;

    WhereStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_WHERE;
    }

    public static class WhereStatementBuilder {
        private Expression expression;

        WhereStatementBuilder() {}

        public WhereStatementBuilder expression(Expression expression) {
            this.expression = expression;
            return this;
        }

        public WhereStatement build() {
            return new WhereStatement(this.expression);
        }

        public String toString() {
            return "WhereStatement.WhereStatementBuilder(expression=" + this.expression + ")";
        }
    }
}
