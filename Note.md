### New feature

    - Create a method that check an entity validity in the EntityManager class

### Thoughts

    - Relationship between entity can be handled without any other annotation as long as a @OneToOne has its @OneToOne couple from the target entity, a @OneToMany has its @ManyToOne, and a @ManyToMany has its @ManyToMany

### Bug

    - Multiple request handling. (test case: 3 requests 2 simultaneous)
    The first request on the first attempt works properly, but the second request and the third request are occuring at the same time (the third request does not retrieve a connection from the pool, meaning it was not closed)

    - Bug resolution hypothesis: The DatabaseConnection is wrapping the connection object inside itself, and all instance of DatabaseConnection are managed by the ConnectionManager (inside a HashMap), so when a request is made, the ConnectionManager retrieve the DatabaseConnection object and return it to the client. The problem is that when accessed simultanously, the same instance is shared and the connection object is overriden by each call, i.e they will use the same instance of conneciton . So if the first call close the connection, the next one will throw the exception telling the connection has already been closed.
    Solution is to find a way to separate the connection object from the DatabaseConnection object.

### Documentation

    - Repository
