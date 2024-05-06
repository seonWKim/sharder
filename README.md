# Sharder
Sharder is a library to easily manage application level database sharding.
                                                                           
[Maven Central](https://central.sonatype.com/artifact/io.github.seonwkim/sharder-core)

## Dependencies 
Pom.xml 
```pom.xml
<dependency>
    <groupId>io.github.seonwkim</groupId>
    <artifactId>sharder-core</artifactId>
    <version>0.0.2</version>
</dependency>
```          

Gradle 
```gradle 
implementation group: 'io.github.seonwkim', name: 'sharder-core', version: '0.0.2'
```


## Features
- Sharder can be configured to route queries to the correct shard based on the query.
- Supported queries
    - SELECT(No JOINS)
    - INSERT
    - UPDATE
    - DELETE
- Supported sharding method
    - Mod
    - Range
    - Hash(TBD)
- Supported Database: MySQL, PostgreSQL(TBD)

## How to use?
- View below example or the [full application example](./example). 

### Shard by Mod
```java
SharderDatabase shard1 = new SharderDatabase() {
    @Override
    public String databaseName() {
        return "shard1";
    }

    @Override
    public List<ShardDefinition> shardDefinitions() {
        return List.of(new ShardDefinitionMod("members.id % 1 = 0"));
    }
};

SharderDatabase shard2 = new SharderDatabase() {
    @Override
    public String databaseName() {
        return "shard2";
    }

    @Override
    public List<ShardDefinition> shardDefinitions() {
        return List.of(new ShardDefinitionMod("members.id % 1 = 1"));
    }
};

var databases = List.of(shard1, shard2);

SimpleQueryShardMatcher matcher = new SimpleQueryShardMatcher();
matcher.match("SELECT * FROM members WHERE id = 0", databases); // this will return shard1  
```

### Shard by Range
```java
SharderDatabase shard1 = new SharderDatabase() {
    @Override
    public String databaseName() {
        return "shard3";
    }

    @Override
    public List<ShardDefinition> shardDefinitions() {
        return List.of(new ShardDefinitionRange("members.id < 50"));
    }
};

SharderDatabase shard2 = new SharderDatabase() {
    @Override
    public String databaseName() {
        return "shard2";
    }

    @Override
    public List<ShardDefinition> shardDefinitions() {
        return List.of(new ShardDefinitionRange("members.id >= 50 AND members.id < 100"));
    }
};

SharderDatabase shard3 = new SharderDatabase() {
    @Override
    public String databaseName() {
        return "shard3";
    }

    @Override
    public List<ShardDefinition> shardDefinitions() {
        return List.of(new ShardDefinitionRange("members.id >= 100"));
    }
};

var databases = List.of(shard1, shard2, shard3);

SimpleQueryShardMatcher matcher = new SimpleQueryShardMatcher();
matcher.match("SELECT * FROM members WHERE id > 80", databases); // this will return shard2 and shard3
```

### Shard by Hash(TBD)
