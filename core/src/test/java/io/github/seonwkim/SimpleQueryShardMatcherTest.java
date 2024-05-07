package io.github.seonwkim;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.seonwkim.shard.DefaultSharderDatabase;
import io.github.seonwkim.shard.ShardDefinitionMod;
import io.github.seonwkim.shard.ShardDefinitionRange;
import io.github.seonwkim.shard.ShardHashFunction;
import io.github.seonwkim.shard.SharderDatabase;

class SimpleQueryShardMatcherTest {

    SimpleQueryShardMatcher matcher = new SimpleQueryShardMatcher();

    @Test
    void shard_should_match_all_when_no_shard_definition_exists_1() {
        final SharderDatabase shard1 = () -> "shard1";
        final SharderDatabase shard2 = () -> "shard2";

        final String query1 = "SELECT * FROM person;";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isTrue();
    }

    @Test
    void shard_should_match_all_when_no_shard_definition_exists() {
        final DefaultSharderDatabase shard1 = new DefaultSharderDatabase("shard1", Collections.emptyList());
        final DefaultSharderDatabase shard2 = new DefaultSharderDatabase("shard2", Collections.emptyList());

        final String query1 = "SELECT * FROM person;";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isTrue();

        final String query2 = "SELECT * FROM person WHERE id = 1;";
        assertThat(matcher.match(query2, shard1)).isTrue();
        assertThat(matcher.match(query2, shard2)).isTrue();
    }

    @Test
    void select_no_where_statement_with_shard_definition_mod() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query = "SELECT * FROM person;";
        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void select_no_where_statement_with_shard_definition_range() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange("person.id >= 10")));

        final String query = "SELECT * FROM person;";
        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void select_with_where_statement_with_no_shard_definition() {
        final String query = "SELECT * FROM person WHERE id = 1";
        final DefaultSharderDatabase shard1 = new DefaultSharderDatabase("shard1", Collections.emptyList());
        final DefaultSharderDatabase shard2 = new DefaultSharderDatabase("shard2", Collections.emptyList());

        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void select_where_statement_and_shard_definition_mod() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query1 = "SELECT * FROM person WHERE id = 0";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "SELECT * FROM person WHERE id = 1";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();

        final String query3 = "SELECT * FROM person WHERE id = 3 OR id = 4";
        assertThat(matcher.match(query3, shard1)).isTrue();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "SELECT * FROM person WHERE id = 3 AND id = 4";
        assertThat(matcher.match(query4, shard1)).isFalse();
        assertThat(matcher.match(query4, shard2)).isFalse();

        final String query5 = "SELECT * FROM person WHERE id = 0 AND name = 'Alice'";
        assertThat(matcher.match(query5, shard1)).isTrue();
        assertThat(matcher.match(query5, shard2)).isFalse();

        final String query6 = "SELECT * FROM person WHERE name = 'Alice'";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isTrue();

        final String query7 = "SELECT * FROM person WHERE id = 0 OR name = 'Alice'";
        assertThat(matcher.match(query7, shard1)).isTrue();
        assertThat(matcher.match(query7, shard2)).isTrue();
    }

    @Test
    void select_where_statement_and_shard_definition_mod_with_long_type() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query1 = "SELECT * FROM person WHERE id = 2147483648";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "SELECT * FROM person WHERE id = 2147483649";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();

        final String query3 = "SELECT * FROM person WHERE id = 2147483648 OR id = 2147483649";
        assertThat(matcher.match(query3, shard1)).isTrue();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "SELECT * FROM person WHERE id = 2147483647 AND id = 2147483648";
        assertThat(matcher.match(query4, shard1)).isFalse();
        assertThat(matcher.match(query4, shard2)).isFalse();

        final String query5 = "SELECT * FROM person WHERE id = 2147483648 AND name = 'Alice'";
        assertThat(matcher.match(query5, shard1)).isTrue();
        assertThat(matcher.match(query5, shard2)).isFalse();

        final String query6 = "SELECT * FROM person WHERE name = 'Alice'";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isTrue();

        final String query7 = "SELECT * FROM person WHERE id = 2147483648 OR name = 'Alice'";
        assertThat(matcher.match(query7, shard1)).isTrue();
        assertThat(matcher.match(query7, shard2)).isTrue();
    }

    @Test
    void select_where_statement_and_shard_definition_mod_with_constant_hash_function() {
        final ShardHashFunction<Long> hashFunction = token -> 0L;
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1",
                                           List.of(new ShardDefinitionMod("person.id % 2 = 0", hashFunction)));

        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2",
                                           List.of(new ShardDefinitionMod("person.id % 2 = 1", hashFunction)));

        final String query1 = "SELECT * FROM person WHERE id = 1"; // hash(1) = 0L
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "SELECT * FROM person WHERE id = 2"; // hash(2) = 0L
        assertThat(matcher.match(query2, shard1)).isTrue();
        assertThat(matcher.match(query2, shard2)).isFalse();
    }

    @Test
    void select_where_statement_and_shard_definition_mod_with_hash_function_adder_hash_function() {
        final ShardHashFunction<Long> hashFunction = token -> Long.parseLong(token.lexeme()) + 1L;
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1",
                                           List.of(new ShardDefinitionMod("person.id % 2 = 0", hashFunction)));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2",
                                           List.of(new ShardDefinitionMod("person.id % 2 = 1", hashFunction)));

        final String query1 = "SELECT * FROM person WHERE id = 1"; // hash(1) = 2L
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "SELECT * FROM person WHERE id = 2"; // hash(2) = 3L
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();
    }

    @Test
    void select_where_statement_and_shard_definition_mod_with_hash_function_mod_hash_function() {
        final ShardHashFunction<Long> hashFunction = token -> Long.parseLong(token.lexeme()) % 2L;
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1",
                                           List.of(new ShardDefinitionMod("person.id % 2 = 0", hashFunction)));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2",
                                           List.of(new ShardDefinitionMod("person.id % 2 = 1", hashFunction)));

        final String query1 = "SELECT * FROM person WHERE id = 2"; // hash(2) = 0L
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "SELECT * FROM person WHERE id = 3"; // hash(3) = 1L
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();
    }

    @Test
    void select_where_statement_and_shard_definition_mod_with_hash_function() {
        final ShardHashFunction<Long> hashFunction = token -> (long) token.lexeme().hashCode();
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1",
                                           List.of(new ShardDefinitionMod("person.name % 2 = 0",
                                                                          hashFunction)));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2",
                                           List.of(new ShardDefinitionMod("person.name % 2 = 1",
                                                                          hashFunction)));

        final long aliceHashCode = "Alice".hashCode();
        final String query1 = "SELECT * FROM person WHERE name = 'Alice'"; // hash('Alice') = 0L
        if (aliceHashCode % 2 == 0) {
            assertThat(matcher.match(query1, shard1)).isTrue();
            assertThat(matcher.match(query1, shard2)).isFalse();
        } else {
            assertThat(matcher.match(query1, shard1)).isFalse();
            assertThat(matcher.match(query1, shard2)).isTrue();
        }
    }

    @Test
    void select_where_statement_and_shard_definition_range() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange("person.id >= 10")));

        final String query1 = "SELECT * FROM person WHERE id < 10";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "SELECT * FROM person WHERE id >= 10";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();

        final String query3 = "SELECT * FROM person WHERE id < 10 OR id >= 10";
        assertThat(matcher.match(query3, shard1)).isTrue();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "SELECT * FROM person WHERE id < 10 AND id >= 10";
        assertThat(matcher.match(query4, shard1)).isFalse();
        assertThat(matcher.match(query4, shard2)).isFalse();

        final String query5 = "SELECT * FROM person WHERE id < 10 AND name = 'Alice'";
        assertThat(matcher.match(query5, shard1)).isTrue();
        assertThat(matcher.match(query5, shard2)).isFalse();

        final String query6 = "SELECT * FROM person WHERE name = 'Alice'";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isTrue();

        final String query7 = "SELECT * FROM person WHERE id < 10 OR name = 'Alice'";
        assertThat(matcher.match(query7, shard1)).isTrue();
        assertThat(matcher.match(query7, shard2)).isTrue();

        final String query8 = "SELECT * FROM person WHERE id > 0 AND id < 10";
        assertThat(matcher.match(query8, shard1)).isTrue();
        assertThat(matcher.match(query8, shard2)).isFalse();

        final String query9 = "SELECT * FROM person WHERE id > 0 AND id < 20";
        assertThat(matcher.match(query9, shard1)).isTrue();
        assertThat(matcher.match(query9, shard2)).isTrue();

        final String query10 = "SELECT * FROM person WHERE id < 0 OR id > 20";
        assertThat(matcher.match(query10, shard1)).isTrue();
        assertThat(matcher.match(query10, shard2)).isTrue();
    }

    @Test
    void select_where_statement_and_multiple_shard_definition_range_1() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange(
                        "person.id >= 10 AND person.id < 20")));
        final DefaultSharderDatabase shard3 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange("person.id >= 20")));

        final String query1 = "SELECT * FROM person WHERE id = 5";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();
        assertThat(matcher.match(query1, shard3)).isFalse();

        final String query2 = "SELECT * FROM person WHERE id = 15";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();
        assertThat(matcher.match(query2, shard3)).isFalse();

        final String query3 = "SELECT * FROM person WHERE id = 25";
        assertThat(matcher.match(query3, shard1)).isFalse();
        assertThat(matcher.match(query3, shard2)).isFalse();
        assertThat(matcher.match(query3, shard3)).isTrue();

        final String query4 = "SELECT * FROM person WHERE id > 0 AND id < 30";
        assertThat(matcher.match(query4, shard1)).isTrue();
        assertThat(matcher.match(query4, shard2)).isTrue();
        assertThat(matcher.match(query4, shard3)).isTrue();

        final String query5 = "SELECT * FROM person WHERE id > 0 AND id < 15";
        assertThat(matcher.match(query5, shard1)).isTrue();
        assertThat(matcher.match(query5, shard2)).isTrue();
        assertThat(matcher.match(query5, shard3)).isFalse();

        final String query6 = "SELECT * FROM person WHERE id < 10 OR id >= 20";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isFalse();
        assertThat(matcher.match(query6, shard3)).isTrue();
    }

    @Test
    void select_where_statement_and_multiple_shard_definition_range_2() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1",
                                           List.of(new ShardDefinitionRange(
                                                   "person.id > 10 AND person.id <= 20")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2",
                                           List.of(new ShardDefinitionRange("person.id <= 10"),
                                                   new ShardDefinitionRange("person.id > 20")));

        final String query1 = "SELECT * FROM person WHERE id = 5";
        assertThat(matcher.match(query1, shard1)).isFalse();
        assertThat(matcher.match(query1, shard2)).isTrue();

        final String query2 = "SELECT * FROM person WHERE id = 15";
        assertThat(matcher.match(query2, shard1)).isTrue();
        assertThat(matcher.match(query2, shard2)).isFalse();

        final String query3 = "SELECT * FROM person WHERE id = 25";
        assertThat(matcher.match(query3, shard1)).isFalse();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "SELECT * FROM person WHERE id > 0 AND id < 30";
        assertThat(matcher.match(query4, shard1)).isTrue();
        assertThat(matcher.match(query4, shard2)).isTrue();

        final String query5 = "SELECT * FROM person WHERE id > 0 AND id < 15";
        assertThat(matcher.match(query5, shard1)).isTrue();
        assertThat(matcher.match(query5, shard2)).isTrue();

        final String query6 = "SELECT * FROM person WHERE id < 10 OR id > 20";
        assertThat(matcher.match(query6, shard1)).isFalse();
        assertThat(matcher.match(query6, shard2)).isTrue();

        final String query7 = "SELECT * FROM person WHERE id >= 0 AND id < 10";
        assertThat(matcher.match(query7, shard1)).isFalse();
        assertThat(matcher.match(query7, shard2)).isTrue();
    }

    @Test
    void insert_no_shard_definition() {
        final DefaultSharderDatabase shard1 = new DefaultSharderDatabase("shard1", Collections.emptyList());
        final DefaultSharderDatabase shard2 = new DefaultSharderDatabase("shard2", Collections.emptyList());

        final String query = "INSERT INTO person (id, name) VALUES (1, 'Alice');";
        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void insert_with_shard_definition_mod() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query1 = "INSERT INTO person (id, name) VALUES (0, 'Alice');";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "INSERT INTO person (id, name) VALUES (1, 'Alice');";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();
    }

    @Test
    void insert_with_shard_definition_range() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange("person.id >= 10")));

        final String query1 = "INSERT INTO person (id, name) VALUES (0, 'Alice');";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "INSERT INTO person (id, name) VALUES (10, 'Alice');";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();

        final String query3 = "INSERT INTO person (id, name) VALUES (15, 'Alice');";
        assertThat(matcher.match(query3, shard1)).isFalse();
        assertThat(matcher.match(query3, shard2)).isTrue();
    }

    @Test
    void update_no_shard_definition() {
        final DefaultSharderDatabase shard1 = new DefaultSharderDatabase("shard1", Collections.emptyList());
        final DefaultSharderDatabase shard2 = new DefaultSharderDatabase("shard2", Collections.emptyList());

        final String query = "UPDATE person SET name = 'Alice' WHERE id = 1;";
        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void update_with_shard_definition_mod() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query1 = "UPDATE person SET name = 'Alice' WHERE id = 0;";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "UPDATE person SET name = 'Alice' WHERE id = 1;";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();

        final String query3 = "UPDATE person SET name = 'Alice', age = 20 WHERE id = 1;";
        assertThat(matcher.match(query3, shard1)).isFalse();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "UPDATE person SET name = 'Alice', age = 20 WHERE id = 0 OR id = 1;";
        assertThat(matcher.match(query4, shard1)).isTrue();
        assertThat(matcher.match(query4, shard2)).isTrue();

        final String query5 = "UPDATE person SET name = 'Alice' WHERE id = 0 AND name = 'Alice';";
        assertThat(matcher.match(query5, shard1)).isTrue();
        assertThat(matcher.match(query5, shard2)).isFalse();

        final String query6 = "UPDATE person SET name = 'Alice' WHERE name = 'Alice';";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isTrue();

        final String query7 = "UPDATE person SET name = 'Alice' WHERE id = 0 OR name = 'Alice';";
        assertThat(matcher.match(query7, shard1)).isTrue();
        assertThat(matcher.match(query7, shard2)).isTrue();

        final String query8 = "UPDATE person SET name = 'Alice' WHERE id = 0 AND id = 1";
        assertThat(matcher.match(query8, shard1)).isFalse();
        assertThat(matcher.match(query8, shard2)).isFalse();
    }

    @Test
    void update_with_shard_definition_range() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange("person.id >= 10")));

        final String query1 = "UPDATE person SET name = 'Alice' WHERE id < 10;";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "UPDATE person SET name = 'Alice' WHERE id >= 10;";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();

        final String query3 = "UPDATE person SET name = 'Alice' WHERE id < 10 OR id >= 10;";
        assertThat(matcher.match(query3, shard1)).isTrue();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "UPDATE person SET name = 'Alice' WHERE age = 20;";
        assertThat(matcher.match(query4, shard1)).isTrue();
        assertThat(matcher.match(query4, shard2)).isTrue();

        final String query5 = "UPDATE person SET name = 'Alice' WHERE id = 15;";
        assertThat(matcher.match(query5, shard1)).isFalse();
        assertThat(matcher.match(query5, shard2)).isTrue();

        final String query6 = "UPDATE person SET name = 'Alice' WHERE id = 5;";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isFalse();
    }

    @Test
    void update_with_multiple_shard_definition_range() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange(
                        "person.id >= 10 AND person.id < 20")));
        final DefaultSharderDatabase shard3 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange("person.id >= 20")));

        final String query1 = "UPDATE person SET name = 'Alice' WHERE id = 5;";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();
        assertThat(matcher.match(query1, shard3)).isFalse();

        final String query2 = "UPDATE person SET name = 'Alice' WHERE id = 15;";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();
        assertThat(matcher.match(query2, shard3)).isFalse();

        final String query3 = "UPDATE person SET name = 'Alice' WHERE id = 25;";
        assertThat(matcher.match(query3, shard1)).isFalse();
        assertThat(matcher.match(query3, shard2)).isFalse();
        assertThat(matcher.match(query3, shard3)).isTrue();

        final String query4 = "UPDATE person SET name = 'Alice' WHERE id > 0 AND id < 30;";
        assertThat(matcher.match(query4, shard1)).isTrue();
        assertThat(matcher.match(query4, shard2)).isTrue();
        assertThat(matcher.match(query4, shard3)).isTrue();

        final String query5 = "UPDATE person SET name = 'Alice' WHERE id > 0 AND id < 15;";
        assertThat(matcher.match(query5, shard1)).isTrue();
        assertThat(matcher.match(query5, shard2)).isTrue();
        assertThat(matcher.match(query5, shard3)).isFalse();
    }

    @Test
    void delete_no_shard_definition_mod() {
        final DefaultSharderDatabase shard1 = new DefaultSharderDatabase("shard1", Collections.emptyList());
        final DefaultSharderDatabase shard2 = new DefaultSharderDatabase("shard2", Collections.emptyList());

        final String query = "DELETE FROM person WHERE id = 1;";
        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void delete_with_shard_definition_mod() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query1 = "DELETE FROM person;";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isTrue();

        final String query2 = "DELETE FROM person WHERE id = 0;";
        assertThat(matcher.match(query2, shard1)).isTrue();
        assertThat(matcher.match(query2, shard2)).isFalse();

        final String query3 = "DELETE FROM person WHERE id = 1;";
        assertThat(matcher.match(query3, shard1)).isFalse();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "DELETE FROM person WHERE id = 0 OR id = 1;";
        assertThat(matcher.match(query4, shard1)).isTrue();
        assertThat(matcher.match(query4, shard2)).isTrue();

        final String query5 = "DELETE FROM person WHERE id = 0 AND id = 1";
        assertThat(matcher.match(query5, shard1)).isFalse();
        assertThat(matcher.match(query5, shard2)).isFalse();

        final String query6 = "DELETE FROM person WHERE id = 0 AND name = 'Alice'";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isFalse();

        final String query7 = "DELETE FROM person WHERE name = 'Alice'";
        assertThat(matcher.match(query7, shard1)).isTrue();
        assertThat(matcher.match(query7, shard2)).isTrue();

        final String query8 = "DELETE FROM person WHERE id = 0 OR name = 'Alice'";
        assertThat(matcher.match(query8, shard1)).isTrue();
        assertThat(matcher.match(query8, shard2)).isTrue();
    }

    @Test
    void delete_with_shard_definition_range() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2", List.of(new ShardDefinitionRange("person.id >= 10")));

        final String query1 = "DELETE FROM person;";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isTrue();

        final String query2 = "DELETE FROM person WHERE id < 10;";
        assertThat(matcher.match(query2, shard1)).isTrue();
        assertThat(matcher.match(query2, shard2)).isFalse();

        final String query3 = "DELETE FROM person WHERE id >= 10;";
        assertThat(matcher.match(query3, shard1)).isFalse();
        assertThat(matcher.match(query3, shard2)).isTrue();

        final String query4 = "DELETE FROM person WHERE id = 5;";
        assertThat(matcher.match(query4, shard1)).isTrue();
        assertThat(matcher.match(query4, shard2)).isFalse();

        final String query5 = "DELETE FROM person WHERE id = 15;";
        assertThat(matcher.match(query5, shard1)).isFalse();
        assertThat(matcher.match(query5, shard2)).isTrue();

        final String query6 = "DELETE FROM person WHERE id < 10 OR id >= 10;";
        assertThat(matcher.match(query6, shard1)).isTrue();
        assertThat(matcher.match(query6, shard2)).isTrue();

        final String query7 = "DELETE FROM person WHERE id < 10 AND id >= 10;";
        assertThat(matcher.match(query7, shard1)).isFalse();
        assertThat(matcher.match(query7, shard2)).isFalse();

        final String query8 = "DELETE FROM person WHERE id < 10 AND name = 'Alice'";
        assertThat(matcher.match(query8, shard1)).isTrue();
        assertThat(matcher.match(query8, shard2)).isFalse();

        final String query9 = "DELETE FROM person WHERE name = 'Alice'";
        assertThat(matcher.match(query9, shard1)).isTrue();
        assertThat(matcher.match(query9, shard2)).isTrue();

        final String query10 = "DELETE FROM person WHERE id < 0 OR id >= 20";
        assertThat(matcher.match(query10, shard1)).isTrue();
        assertThat(matcher.match(query10, shard2)).isTrue();
    }
}
