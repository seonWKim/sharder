package io.github.seonWKim.shard;



import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShardDefinitionModTest {

    @Test
    void shard_definition_test_with_table_name() {
        ShardDefinitionMod shardDefinitionMod = new ShardDefinitionMod("members.id % 2 = 0");

        assertThat(shardDefinitionMod.tableName()).isEqualTo("members");
        assertThat(shardDefinitionMod.columnName()).isEqualTo("id");
        Assertions.assertThat(shardDefinitionMod.getDivisor().lexeme()).isEqualTo("2");
        Assertions.assertThat(shardDefinitionMod.getResult().lexeme()).isEqualTo("0");
    }

    @Test
    void shard_definition_wrong_case() {
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("id % 2 == 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("2 % 2 = 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("id % invalid = 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("id = 0"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionMod("members.id.id % 2 = 0"));
    }
}
