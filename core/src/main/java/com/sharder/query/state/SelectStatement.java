package com.sharder.query.state;

import java.util.List;

import com.sharder.Statement;
import com.sharder.StatementType;

import com.sharder.Nullable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SelectStatement extends Statement {
    @Builder.Default
    private final boolean selectStar = false;
    @Builder.Default
    private final List<String> fields = List.of();
    @Nullable
    private final String schemaName;
    private final String tableName;

    @Override
    public <R> R accept(Visitor<R> visitor) {

        return visitor.visitStatement(this, StatementType.QUERY_SELECT);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_SELECT;
    }
}
