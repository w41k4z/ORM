package proj.w41k4z.orm.enumeration;

/**
 * An enumeration of the different inheritance types.
 */
public enum InheritanceType {
    /**
     * The inheritance is done in the same table. This must be accompanied by a
     * discriminator column and a discriminator value for each child.
     */
    SAME_TABLE,

    /**
     * The inheritance is done in different tables. There is a constraint for this
     * relationship: the child's primary key must be a foreign key to the parent's
     * primary key. To be short, the can not have a primary key of its own (no
     * column annotated with the @Id) because it is already in the parent entity.
     */
    JOINED_TABLE,

    /**
     * The inheritance is done in different tables too. This is commonly used when
     * some tables have the same structure but different usage.
     */
    DIFFERENT_TABLE
}
