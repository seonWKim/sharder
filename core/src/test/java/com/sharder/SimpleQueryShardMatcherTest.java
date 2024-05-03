package com.sharder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sharder.shard.DefaultSharderDatabase;
import com.sharder.shard.ShardDefinitionMod;
import com.sharder.shard.ShardDefinitionRange;

class SimpleQueryShardMatcherTest {

    SimpleQueryShardMatcher matcher = new SimpleQueryShardMatcher();

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
    void select_where_statement_and_multiple_shard_definition_range() {
        final DefaultSharderDatabase shard1 =
                new DefaultSharderDatabase("shard1", List.of(new ShardDefinitionRange("person.id < 10")));
        final DefaultSharderDatabase shard2 =
                new DefaultSharderDatabase("shard2",
                                           List.of(new ShardDefinitionRange("person.id >= 10"),
                                                   new ShardDefinitionRange("person.id < 20")));
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
                new DefaultSharderDatabase("shard2",
                                           List.of(new ShardDefinitionRange("person.id >= 10"),
                                                   new ShardDefinitionRange("person.id < 20")));
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
