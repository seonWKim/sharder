# Sharder
Sharder is a library to easily manage application level database sharding.
<div align="center">
  <img src="./icon.png" width="150" height="150">
</div>

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
