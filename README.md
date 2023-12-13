## EntityManager usage

    - Create a new instance of the EntityManager class
    - Create a new instance of the QueryExecutor class
    - Create a new instance of the NativeQueryBuilder class (Can be done by using OQL class too)
    - [Optional] Specify the database connection to use via the EntityManager.Transaction.use(String connecitonName) method. (Note: the database connection passed from the EntityManager constructor will be used by default)
    - Create a new instance of the EntityMapper using the result of the query execution with the current database connection from the entity manager transaction created above
    - Commit or rollback the transaction
    - Close the transaction

## Relationship usage

    - Be aware of the relationship types (Careful when choosing between OneToMany and ManyToMany as quite a few things change)
    - Read doc
