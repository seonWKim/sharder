package io.github.seonwkim.query.state;

import java.util.List;

import io.github.seonwkim.StatementType;
import io.github.seonwkim.FirstStatement;
import io.github.seonwkim.Nullable;

import lombok.Getter;

/**
 * Represents a SELECT statement in a query. Note that {@code schemaName} is optional.<br>
 * e.g. SELECT column1, column2 FROM schema.table_name;
 */
@Getter
public class SelectStatement extends FirstStatement {
    public static SelectStatementBuilder builder() {return new SelectStatementBuilder();}

    private final boolean selectStar;
    private final List<String> fields;
    @Nullable
    private final String schemaName;
    private final String tableName;

    SelectStatement(boolean selectStar, List<String> fields, @Nullable String schemaName, String tableName) {
        this.selectStar = selectStar;
        this.fields = fields;
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    private static boolean $default$selectStar() {return false;}

    private static List<String> $default$fields() {return List.of();}

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_SELECT;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    public static class SelectStatementBuilder {
        private boolean selectStar$value;
        private boolean selectStar$set;
        private List<String> fields$value;
        private boolean fields$set;
        private @Nullable String schemaName;
        private String tableName;

        SelectStatementBuilder() {}

        public SelectStatementBuilder selectStar(boolean selectStar) {
            this.selectStar$value = selectStar;
            this.selectStar$set = true;
            return this;
        }

        public SelectStatementBuilder fields(List<String> fields) {
            this.fields$value = fields;
            this.fields$set = true;
            return this;
        }

        public SelectStatementBuilder schemaName(@Nullable String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public SelectStatementBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public SelectStatement build() {
            boolean selectStar$value = this.selectStar$value;
            if (!this.selectStar$set) {
                selectStar$value = SelectStatement.$default$selectStar();
            }
            List<String> fields$value = this.fields$value;
            if (!this.fields$set) {
                fields$value = SelectStatement.$default$fields();
            }
            return new SelectStatement(selectStar$value, fields$value, this.schemaName, this.tableName);
        }

        public String toString() {
            return "SelectStatement.SelectStatementBuilder(selectStar$value=" + this.selectStar$value
                   + ", fields$value=" + this.fields$value + ", schemaName=" + this.schemaName + ", tableName="
                   + this.tableName + ")";
        }
    }
}
