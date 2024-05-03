package com.sharder;

public enum StatementType {
    QUERY_SELECT,
    QUERY_INSERT,
    QUERY_UPDATE,
    QUERY_DELETE,
    QUERY_WHERE,

    EXPR,

    // TODO: add shard related types
}
