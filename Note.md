### New feature

    - Create a method that check an entity validity in the EntityManager class

### Thoughts

    - Relationship between entity can be handled without any other annotation as long as a @OneToOne has its @OneToOne couple from the target entity, a @OneToMany has its @ManyToOne, and a @ManyToMany has its @ManyToMany

### Bug

    - Multiple request handling. (test case: 3 requests 2 simultaneous)
    The first request on the first attempt works properly, but the second request and the third request are occuring at the same time (the third request does not retrieve a connection from the pool, meaning it was not closed)

### Documentation

    - Repository
