package io.github.seonwkim;

/**
 * Represents the first statement in a query such as SELECT, INSERT, UPDATE, DELETE.
 */
public abstract class FirstStatement extends Statement {
    public abstract String tableName();
}
