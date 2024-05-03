package com.sharder.shard;



import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ShardDefinitionModTest {

    @Test
    void shard_definition_test_with_table_name() {
        ShardDefinitionMod shardDefinitionMod = new ShardDefinitionMod("db.id % 2 = 0");

        assertThat(shardDefinitionMod.tableName()).isEqualTo("db");
        assertThat(shardDefinitionMod.columnName()).isEqualTo("id");
        assertThat(shardDefinitionMod.getDivisor().lexeme()).isEqualTo("2");
        assertThat(shardDefinitionMod.getResult().lexeme()).isEqualTo("0");
    }

    @Test
    void shard_definition_wrong_case() {
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("id % 2 == 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("2 % 2 = 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("id % databaseName = 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("id = 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("db.id.id % 2 = 0"));
    }
}
