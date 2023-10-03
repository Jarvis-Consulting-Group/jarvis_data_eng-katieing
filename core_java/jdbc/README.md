# Introduction

This JDBC app connects to a PSQL database and allows the user to perform CRUD operations to the database from Java.
The app connects to a sales database containing order, product, customer, and sales representative data for a retail store.
Create, read, update, and delete operations are available for the customer table as well as read operations for the order and order item tables.
This app uses Maven for build management.

# Implementaiton
## ER Diagram
![ER Diagram](./Diagrams/JDBC_ERD.png)

## Design Patterns

This app uses a DAO design pattern. A summary and comparison of DAO and Repository design patterns is below: 

#### DAO
In the DAO design pattern, DAO classes are used for each entity (eg. CustomerDAO, OrderDao) and
the methods based on each CRUD operation are created to directly reflect the RDBMS structure. 
Table joins and complex querying is done at the 
database level.

#### Repository
In the repository design pattern, a repository class for each entity is used with higher level entity operations
(eg. findOrderById(), findOrderLines()). This results in less complex queries being executed on PSQL, with
operations on the data likely taking place within the JDBC app. 


# Test
This app was tested with sample data inserted into the database and verifying query results on PSQL.