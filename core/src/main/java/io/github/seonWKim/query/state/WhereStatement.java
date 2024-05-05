package io.github.seonWKim.query.state;

import io.github.seonWKim.Expression;
import io.github.seonWKim.StatementType;
import io.github.seonWKim.Statement;

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
