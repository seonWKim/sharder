package io.github.seonwkim.query.state;

import io.github.seonwkim.Statement;
import io.github.seonwkim.StatementType;
import lombok.Getter;

/**
 * Represents a FROM statement in SQL. Note that {@code schemaName} is optional.<br>
 * e.g. FROM schema.table_name;
 */
@Getter
public class FromStatement extends Statement {

    public static FromStatementBuilder builder() {return new FromStatementBuilder();}

    private final String schemaName;
    private final String tableName;

    FromStatement(String schemaName, String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_FROM;
    }

    public static class FromStatementBuilder {
        private String schemaName;
        private String tableName;

        FromStatementBuilder() {}

        public FromStatementBuilder schemaName(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public FromStatementBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public FromStatement build() {
            return new FromStatement(this.schemaName, this.tableName);
        }

        public String toString() {
            return "FromStatement.FromStatementBuilder(schemaName=" + this.schemaName + ", tableName="
                   + this.tableName + ")";
        }
    }
}
