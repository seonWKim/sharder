package com.seonWKim.sharder;

/**
 * Represents a statement type in a query.
 */
public enum StatementType {
    QUERY_SELECT,
    QUERY_INSERT,
    QUERY_UPDATE,
    QUERY_DELETE,
    QUERY_WHERE,

    EXPR,
}
