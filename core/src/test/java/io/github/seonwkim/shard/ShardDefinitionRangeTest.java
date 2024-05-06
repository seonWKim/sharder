package io.github.seonwkim.shard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class ShardDefinitionRangeTest {

    private static final String MINUS_INFINITY = new BigDecimal(Long.MIN_VALUE).toString();
    private static final String PLUS_INFINITY = new BigDecimal(Long.MAX_VALUE).toString();

    @Test
    void shard_definition_test_single_condition_1() {
        ShardDefinitionRange shardDefinitionRange = new ShardDefinitionRange("members.id < 10");

        assertThat(shardDefinitionRange.tableName()).isEqualTo("members");
        assertThat(shardDefinitionRange.columnName()).isEqualTo("id");

        assertThat(shardDefinitionRange.getConditions().getLeft().getColumn().lexeme()).isEqualTo("id");
        assertThat(shardDefinitionRange.getConditions().getLeft().getOperator().lexeme()).isEqualTo(">=");
        assertThat(shardDefinitionRange.getConditions().getLeft().getValue().lexeme()).isEqualTo(MINUS_INFINITY);

        assertThat(shardDefinitionRange.getConditions().getRight().getColumn().lexeme()).isEqualTo("id");
        assertThat(shardDefinitionRange.getConditions().getRight().getOperator().lexeme()).isEqualTo("<");
        assertThat(shardDefinitionRange.getConditions().getRight().getValue().lexeme()).isEqualTo("10");
    }

    @Test
    void shard_definition_test_single_condition_2() {
        ShardDefinitionRange shardDefinitionRange = new ShardDefinitionRange("members.id >= 10");

        assertThat(shardDefinitionRange.tableName()).isEqualTo("members");
        assertThat(shardDefinitionRange.columnName()).isEqualTo("id");

        assertThat(shardDefinitionRange.getConditions().getLeft().getColumn().lexeme()).isEqualTo("id");
        assertThat(shardDefinitionRange.getConditions().getLeft().getOperator().lexeme()).isEqualTo(">=");
        assertThat(shardDefinitionRange.getConditions().getLeft().getValue().lexeme()).isEqualTo("10");

        assertThat(shardDefinitionRange.getConditions().getRight().getColumn().lexeme()).isEqualTo("id");
        assertThat(shardDefinitionRange.getConditions().getRight().getOperator().lexeme()).isEqualTo("<=");
        assertThat(shardDefinitionRange.getConditions().getRight().getValue().lexeme()).isEqualTo(PLUS_INFINITY);
    }

    @Test
    void shard_definition_test_multiple_condition() {
        ShardDefinitionRange shardDefinitionRange = new ShardDefinitionRange("members.id > 10 AND members.id <= 20");

        assertThat(shardDefinitionRange.tableName()).isEqualTo("members");
        assertThat(shardDefinitionRange.columnName()).isEqualTo("id");

        assertThat(shardDefinitionRange.getConditions().getLeft().getColumn().lexeme()).isEqualTo("id");
        assertThat(shardDefinitionRange.getConditions().getLeft().getOperator().lexeme()).isEqualTo(">");
        assertThat(shardDefinitionRange.getConditions().getLeft().getValue().lexeme()).isEqualTo("10");

        assertThat(shardDefinitionRange.getConditions().getRight().getColumn().lexeme()).isEqualTo("id");
        assertThat(shardDefinitionRange.getConditions().getRight().getOperator().lexeme()).isEqualTo("<=");
        assertThat(shardDefinitionRange.getConditions().getRight().getValue().lexeme()).isEqualTo("20");
    }

    @Test
    void shard_definition_wrong_case() {
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("id < 10"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("10 < 10"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("id < invalid"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("id = 10"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("members.id.id < 10"));

        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("members.id < 10 OR members.id >= 20"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("members.id >= 10 AND members.id > 20 AND members.id > 30"));
        assertThrows(IllegalArgumentException.class, () -> new ShardDefinitionRange("members.id > 10 AND orders.id < 20"));
    }
}
