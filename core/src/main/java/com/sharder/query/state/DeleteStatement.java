package com.sharder.query.state;

import com.sharder.Statement;
import com.sharder.StatementType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeleteStatement extends Statement {

    private final String schemaName;
    private final String tableName;

    @Override
    public <R> R accept(Visitor<R> visitor) {

        return visitor.visitStatement(this, StatementType.QUERY_DELETE);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_DELETE;
    }
}
