package com.sharder.query.state;

import com.sharder.Statement;
import com.sharder.StatementType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
class InsertStatement extends Statement {
    @Override
    public <R> R accept(Visitor<R> visitor) {

        return visitor.visitStatement(this, StatementType.QUERY_INSERT);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_INSERT;
    }
}
