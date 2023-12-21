### New feature

    - Create a method that check an entity validity in the EntityManager class

### Thoughts

    - Relationship between entity can be handled without any other annotation as long as a @OneToOne has its @OneToOne couple from the target entity, a @OneToMany has its @ManyToOne, and a @ManyToMany has its @ManyToMany

### Bug

    - When a @OneToOne collapse with a @OneToMany/@ManyToMany is the same entity as the same related entity, the related entity columns are duplicated in the request generated

    - Same table inheritance when inserting is forgetting the discriminator column and values

### Documentation

    - Repository
