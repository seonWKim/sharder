package com.sharder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sharder.shard.ShardDefinitionMod;
import com.sharder.shard.SharderDatabase;

class SimpleQueryShardMatcherTest {

    SimpleQueryShardMatcher matcher = new SimpleQueryShardMatcher();

    @Test
    void select_no_where_statement() {
        final SharderDatabase shard1 =
                new SharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final SharderDatabase shard2 =
                new SharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query = "SELECT * FROM person;";
        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void select_no_shard_definition() {
        final String query = "SELECT * FROM person WHERE id = 1";
        final SharderDatabase shard1 = new SharderDatabase("shard1", Collections.emptyList());
        final SharderDatabase shard2 = new SharderDatabase("shard2", Collections.emptyList());

        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void select_no_where_statement_and_shard_definition() {
        final String query = "SELECT * FROM person;";
        final SharderDatabase shard1 =
                new SharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final SharderDatabase shard2 =
                new SharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void select_where_statement_and_shard_definition_1() {
        final SharderDatabase shard1 =
                new SharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final SharderDatabase shard2 =
                new SharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

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
    void insert_no_shard_definition() {
        final SharderDatabase shard1 = new SharderDatabase("shard1", Collections.emptyList());
        final SharderDatabase shard2 = new SharderDatabase("shard2", Collections.emptyList());

        final String query = "INSERT INTO person (id, name) VALUES (1, 'Alice');";
        assertThat(matcher.match(query, shard1)).isTrue();
        assertThat(matcher.match(query, shard2)).isTrue();
    }

    @Test
    void insert_shard_definition_1() {
        final SharderDatabase shard1 =
                new SharderDatabase("shard1", List.of(new ShardDefinitionMod("person.id % 2 = 0")));
        final SharderDatabase shard2 =
                new SharderDatabase("shard2", List.of(new ShardDefinitionMod("person.id % 2 = 1")));

        final String query1 = "INSERT INTO person (id, name) VALUES (0, 'Alice');";
        assertThat(matcher.match(query1, shard1)).isTrue();
        assertThat(matcher.match(query1, shard2)).isFalse();

        final String query2 = "INSERT INTO person (id, name) VALUES (1, 'Alice');";
        assertThat(matcher.match(query2, shard1)).isFalse();
        assertThat(matcher.match(query2, shard2)).isTrue();
    }
}
