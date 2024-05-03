package com.sharder.query.state;

import com.sharder.Expression;
import com.sharder.Statement;
import com.sharder.StatementType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WhereStatement extends Statement {

    private final Expression expression;

    @Override
    public <R> R accept(Visitor<R> visitor) {
       return visitor.visitStatement(this, StatementType.QUERY_WHERE);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_WHERE;
    }
}
