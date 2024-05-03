package com.sharder.query.state;

import java.util.List;

import com.sharder.FirstStatement;
import com.sharder.StatementType;
import com.sharder.Token;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateStatement extends FirstStatement {
    private final String schemaName;
    private final String tableName;
    private final List<Token> columns;
    private final List<Token> values;

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitStatement(this, StatementType.QUERY_UPDATE);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_UPDATE;
    }

    @Override
    public String tableName() {
        return tableName;
    }
}
