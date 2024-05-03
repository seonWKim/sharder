package com.sharder;

public abstract class Expression {
    public interface Visitor<R> {}

    protected abstract <R> R accept(Visitor<R> visitor);

    public abstract ExpressionType getExpressionType();
}
