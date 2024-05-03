package com.sharder.shard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ShardDefinitionRangeTest {

    @Test
    void shard_definition_test_with_table_name() {
        ShardDefinitionRange shardDefinitionRange = new ShardDefinitionRange("members.id < 10");

        assertThat(shardDefinitionRange.tableName()).isEqualTo("members");
        assertThat(shardDefinitionRange.columnName()).isEqualTo("id");
        assertThat(shardDefinitionRange.getOperator().lexeme()).isEqualTo("<");
        assertThat(shardDefinitionRange.getValue().lexeme()).isEqualTo("10");
    }

    @Test
    void shard_definition_wrong_case() {
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("id < 10"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("10 < 10"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("id < invalid"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("id = 10"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("members.id.id < 10"));
    }
}
