package io.github.seonwkim;

/**
 * Represents a statement type in a query.
 */
public enum StatementType {
    QUERY_SELECT,
    QUERY_INSERT,
    QUERY_UPDATE,
    QUERY_DELETE,
    QUERY_FROM,
    QUERY_WHERE,

    EXPR,
}
