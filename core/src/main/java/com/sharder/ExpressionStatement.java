package com.sharder;

import lombok.Getter;

@Getter
public class ExpressionStatement extends Statement {
    public static ExpressionStatementBuilder builder() {return new ExpressionStatementBuilder();}

    final Expression expression;

    ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.EXPR;
    }

    public static class ExpressionStatementBuilder {
        private Expression expression;

        ExpressionStatementBuilder() {}

        public ExpressionStatementBuilder expression(Expression expression) {
            this.expression = expression;
            return this;
        }

        public ExpressionStatement build() {
            return new ExpressionStatement(this.expression);
        }

        public String toString() {
            return "ExpressionStatement.ExpressionStatementBuilder(expression=" + this.expression + ")";
        }
    }
}
