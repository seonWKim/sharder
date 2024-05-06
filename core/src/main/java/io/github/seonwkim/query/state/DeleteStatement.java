package io.github.seonwkim.query.state;

import io.github.seonwkim.StatementType;
import io.github.seonwkim.StatementWithTableMeta;
import lombok.Getter;

/**
 * Represents a DELETE statement in SQL. Note that {@code schemaName} is optional.<br>
 * e.g. DELETE FROM schema.table_name;
 */
@Getter
public class DeleteStatement extends StatementWithTableMeta {

    public static DeleteStatementBuilder builder() {return new DeleteStatementBuilder();}

    private final FromStatement fromStatement;

    DeleteStatement(FromStatement fromStatement) {
        this.fromStatement = fromStatement;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_DELETE;
    }

    @Override
    public String schemaName() {
        return fromStatement.getSchemaName();
    }

    @Override
    public String tableName() {
        return fromStatement.getTableName();
    }

    public static class DeleteStatementBuilder {
        private FromStatement fromStatement;

        DeleteStatementBuilder() {}

        public DeleteStatementBuilder fromStatement(FromStatement fromStatement) {
            this.fromStatement = fromStatement;
            return this;
        }

        public DeleteStatement build() {
            return new DeleteStatement(this.fromStatement);
        }

        public String toString() {
            return "DeleteStatement.DeleteStatementBuilder(fromStatement=" + this.fromStatement + ")";
        }
    }
}
