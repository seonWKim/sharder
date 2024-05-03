package com.sharder;

/**
 * Represents a statement in a query, shard definition(TBD).
 */
public abstract class Statement {

    public interface Visitor<R> {
        R visitStatement(Statement statement, StatementType type);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public abstract StatementType getStatementType();
}
