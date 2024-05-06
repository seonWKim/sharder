package io.github.seonwkim;

/**
 * Represents the statements including tableName such as SELECT, INSERT, UPDATE, DELETE.
 */
public abstract class StatementWithTableMeta extends Statement {
    @Nullable
    public abstract String schemaName();

    public abstract String tableName();
}
