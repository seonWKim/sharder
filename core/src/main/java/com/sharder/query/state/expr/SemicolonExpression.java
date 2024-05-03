package com.sharder.query.state.expr;

import com.sharder.Expression;
import com.sharder.ExpressionType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SemicolonExpression extends Expression {
    @Override
    protected <R> R accept(Visitor<R> visitor) {
        return null;
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.SEMICOLON;
    }
}
