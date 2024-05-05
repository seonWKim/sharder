# Sharder
Sharder is a library to easily manage application level database sharding.

<br>

<div align="center">
  <img src="./icon.png" width="250" height="250">
</div>
 
<br> 

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
- View the [example](./example) for a simple example of how to use Sharder.
- 
