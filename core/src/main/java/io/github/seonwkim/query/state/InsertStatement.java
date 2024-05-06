package io.github.seonwkim.query.state;

import java.util.ArrayList;
import java.util.List;

import io.github.seonwkim.StatementType;
import io.github.seonwkim.StatementWithTableMeta;
import io.github.seonwkim.Nullable;
import io.github.seonwkim.Token;
import io.github.seonwkim.query.state.expr.ConditionExpression;

import lombok.Getter;

/**
 * Represents an insert statement. Note that {@code schemaName} is optional.<br>
 * e.g. INSERT INTO schema.table_name (column1, column2) VALUES (value1, value2);
 */
@Getter
public class InsertStatement extends StatementWithTableMeta {
    public static InsertStatementBuilder builder() {return new InsertStatementBuilder();}

    private final List<Token> columns;
    private final List<Token> values;
    @Nullable
    private final String schemaName;
    private final String tableName;
    private final ConditionExpression conditionExpression;

    public InsertStatement(List<Token> columns, List<Token> values, String schemaName, String tableName) {
        if (columns.size() != values.size()) {
            throw new IllegalArgumentException("columns and values must have the same size");
        }
        this.columns = columns;
        this.values = values;
        this.schemaName = schemaName;
        this.tableName = tableName;

        final int size = columns.size();
        List<Token> tokensForConditionExpression = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            tokensForConditionExpression.add(columns.get(i));
            tokensForConditionExpression.add(Token.EQUAL);
            tokensForConditionExpression.add(values.get(i));

            if (i < size - 1) {
                tokensForConditionExpression.add(Token.AND);
            }
        }
        this.conditionExpression = new ConditionExpression(tokensForConditionExpression);
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.QUERY_INSERT;
    }

    @Override
    public String schemaName() {
        return schemaName;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    public static class InsertStatementBuilder {
        private List<Token> columns;
        private List<Token> values;
        private @Nullable String schemaName;
        private String tableName;

        InsertStatementBuilder() {}

        public InsertStatementBuilder columns(List<Token> columns) {
            this.columns = columns;
            return this;
        }

        public InsertStatementBuilder values(List<Token> values) {
            this.values = values;
            return this;
        }

        public InsertStatementBuilder schemaName(@Nullable String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public InsertStatementBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public InsertStatement build() {
            return new InsertStatement(this.columns, this.values, this.schemaName, this.tableName);
        }

        public String toString() {
            return "InsertStatement.InsertStatementBuilder(columns=" + this.columns + ", values=" + this.values
                   + ", schemaName=" + this.schemaName + ", tableName=" + this.tableName + ", expression="
                   + ")";
        }
    }
}
