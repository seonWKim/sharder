package com.seonWKim.sharder.query.state;

import com.seonWKim.sharder.StatementType;
import com.seonWKim.sharder.FirstStatement;

import lombok.Getter;

/**
 * Represents a DELETE statement in SQL. Note that {@code schemaName} is optional.<br>
 * e.g. DELETE FROM schema.table_name;
 */
@Getter
public class DeleteStatement extends FirstStatement {
    public static DeleteStatementBuilder builder() {return new DeleteStatementBuilder();}

    private final String schemaName;
    private final String tableName;

    DeleteStatement(String schemaName, String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_DELETE;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    public static class DeleteStatementBuilder {
        private String schemaName;
        private String tableName;

        DeleteStatementBuilder() {}

        public DeleteStatementBuilder schemaName(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public DeleteStatementBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public DeleteStatement build() {
            return new DeleteStatement(this.schemaName, this.tableName);
        }

        public String toString() {
            return "DeleteStatement.DeleteStatementBuilder(schemaName=" + this.schemaName + ", tableName="
                   + this.tableName + ")";
        }
    }
}
