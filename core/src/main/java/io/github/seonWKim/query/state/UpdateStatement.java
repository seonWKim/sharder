package io.github.seonWKim.query.state;

import java.util.List;

import io.github.seonWKim.StatementType;
import io.github.seonWKim.FirstStatement;
import io.github.seonWKim.Token;

import lombok.Getter;

/**
 * Represents an UPDATE statement in a query. Note that {@code schemaName} is optional.<br>
 * e.g. UPDATE schema.table_name SET column1 = value1, column2 = value2;
 */
@Getter
public class UpdateStatement extends FirstStatement {
    public static UpdateStatementBuilder builder() {return new UpdateStatementBuilder();}

    private final String schemaName;
    private final String tableName;
    private final List<Token> columns;
    private final List<Token> values;

    UpdateStatement(String schemaName, String tableName, List<Token> columns, List<Token> values) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_UPDATE;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    public static class UpdateStatementBuilder {
        private String schemaName;
        private String tableName;
        private List<Token> columns;
        private List<Token> values;

        UpdateStatementBuilder() {}

        public UpdateStatementBuilder schemaName(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public UpdateStatementBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public UpdateStatementBuilder columns(List<Token> columns) {
            this.columns = columns;
            return this;
        }

        public UpdateStatementBuilder values(List<Token> values) {
            this.values = values;
            return this;
        }

        public UpdateStatement build() {
            return new UpdateStatement(this.schemaName, this.tableName, this.columns, this.values);
        }

        public String toString() {
            return "UpdateStatement.UpdateStatementBuilder(schemaName=" + this.schemaName + ", tableName="
                   + this.tableName + ", columns=" + this.columns + ", values=" + this.values + ")";
        }
    }
}
