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
    void no_where_statement() {
        final String query = "SELECT * FROM person;";
        final List<SharderDatabase> databases =
                List.of(new SharderDatabase("shard1",
                                            List.of(new ShardDefinitionMod("person.id % 2 = 0"))),
                        new SharderDatabase("shard2",
                                            List.of(new ShardDefinitionMod("person.id % 2 = 1"))));

        assertThat(matcher.match(query, databases)).isEqualTo(databases);
    }

    @Test
    void no_shard_definition() {
        final String query = "SELECT * FROM person WHERE id = 1";
        final List<SharderDatabase> databases = List.of(
                new SharderDatabase("shard1", Collections.emptyList()),
                new SharderDatabase("shard2", Collections.emptyList()));

        assertThat(matcher.match(query, databases)).isEqualTo(databases);
    }

    @Test
    void no_where_statement_and_shard_definition() {
        final String query = "SELECT * FROM person;";
        final List<SharderDatabase> databases = List.of(
                new SharderDatabase("shard1", Collections.emptyList()),
                new SharderDatabase("shard2", Collections.emptyList()));

        assertThat(matcher.match(query, databases)).isEqualTo(databases);
    }

    @Test
    void where_statement_and_shard_definition_1() {
        final List<SharderDatabase> databases =
                List.of(new SharderDatabase("shard1",
                                            List.of(new ShardDefinitionMod("person.id % 2 = 0"))),
                        new SharderDatabase("shard2",
                                            List.of(new ShardDefinitionMod("person.id % 2 = 1"))));

        final String query1 = "SELECT * FROM person WHERE id = 0";
        final List<SharderDatabase> result1 = matcher.match(query1, databases);
        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.get(0).databaseName()).isEqualTo("shard1");

        final String query2 = "SELECT * FROM person WHERE id = 1";
        final List<SharderDatabase> result2 = matcher.match(query2, databases);
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.get(0).databaseName()).isEqualTo("shard2");

        final String query3 = "SELECT * FROM person WHERE id = 3 OR id = 4";
        final List<SharderDatabase> result3 = matcher.match(query3, databases);
        assertThat(result3.size()).isEqualTo(2);

        final String query4 = "SELECT * FROM person WHERE id = 3 AND id = 4";
        final List<SharderDatabase> result4 = matcher.match(query4, databases);
        assertThat(result4.size()).isEqualTo(0);

        final String query5 = "SELECT * FROM person WHERE id = 0 AND name = 'Alice'";
        final List<SharderDatabase> result5 = matcher.match(query5, databases);
        assertThat(result5.size()).isEqualTo(1);
        assertThat(result5.get(0).databaseName()).isEqualTo("shard1");

        final String query6 = "SELECT * FROM person WHERE name = 'Alice'";
        final List<SharderDatabase> result6 = matcher.match(query6, databases);
        assertThat(result6.size()).isEqualTo(2);
    }
}
