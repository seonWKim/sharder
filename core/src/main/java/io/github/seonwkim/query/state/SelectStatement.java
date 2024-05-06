package io.github.seonwkim.query.state;

import java.util.List;

import io.github.seonwkim.StatementType;
import io.github.seonwkim.StatementWithTableMeta;
import lombok.Getter;

/**
 * Represents a SELECT statement in a query. Note that {@code schemaName} is optional.<br>
 * e.g. SELECT column1, column2 FROM schema.table_name;
 */
@Getter
public class SelectStatement extends StatementWithTableMeta {

    public static SelectStatementBuilder builder() {return new SelectStatementBuilder();}

    private final boolean selectStar;
    private final List<String> fields;
    private final FromStatement fromStatement;

    SelectStatement(boolean selectStar, List<String> fields, FromStatement fromStatement) {
        this.selectStar = selectStar;
        this.fields = fields;
        this.fromStatement = fromStatement;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_SELECT;
    }

    @Override
    public String schemaName() {
        return fromStatement.getSchemaName();
    }

    @Override
    public String tableName() {
        return fromStatement.getTableName();
    }

    public static class SelectStatementBuilder {
        private boolean selectStar;
        private List<String> fields;
        private FromStatement fromStatement;

        SelectStatementBuilder() {}

        public SelectStatementBuilder selectStar(boolean selectStar) {
            this.selectStar = selectStar;
            return this;
        }

        public SelectStatementBuilder fields(List<String> fields) {
            this.fields = fields;
            return this;
        }

        public SelectStatementBuilder fromStatement(FromStatement fromStatement) {
            this.fromStatement = fromStatement;
            return this;
        }

        public SelectStatement build() {
            return new SelectStatement(this.selectStar, this.fields, this.fromStatement);
        }

        public String toString() {
            return "SelectStatement.SelectStatementBuilder(selectStar=" + this.selectStar + ", fields="
                   + this.fields + ", fromStatement=" + this.fromStatement + ")";
        }
    }
}
