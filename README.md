# Multithreaded Java XML Server.

This project was made for school, where based on XML input we have to process it.
There is a generator that makes up to 800 connections and talks over TCP.

It is a multithreading and networking exercise. Current it can do the following:

- Create multithreaded network connections.
- Read the XML data over TCP and place it in a Queue.
- Process XML data.
- Create database connection and get all station ids.


## How-to

1. Download the generator from https://blackboard.hanze.nl
2. import/execute `create_table.sql`
3. Compile JavaClient
4. Start JavaClient
5. Start Generator 2.1 or 2.2
6. Enjoy a lot of data in your database.
