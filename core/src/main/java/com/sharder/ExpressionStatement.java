package com.sharder;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExpressionStatement extends Statement {
    final Expression expression;

    final
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitStatement(this, StatementType.EXPR);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.EXPR;
    }
}
