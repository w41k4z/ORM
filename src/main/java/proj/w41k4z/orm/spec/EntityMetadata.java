package proj.w41k4z.orm.spec;

/**
 * {@code EntityMetadata} is a class used to store information about an entity.
 */
public class EntityMetadata {

    private Class<?> entityClass;

    // [0] is the source entity class, [>0] are related entity classes
    private Class<?>[] relatedEntityClasses;

    private EntityChild[] relatedEntityChildren;

    // Excludes entity field with relationship annotation.
    // Used with SELECT query
    private EntityField[] allEntityFields;

    // Main entity fields, includes entity field with relationship annotation.
    // Used with INSERT, UPDATE and DELETE query
    private EntityField[] entityFields;

    /**
     * Default constructor, meant to be instatiated by the {@code EntityManager}
     * class.
     * 
     * @param entityClass the entity class
     */
    protected EntityMetadata(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.relatedEntityClasses = EntityAccess.getRelatedEntityClasses(entityClass);
        this.relatedEntityChildren = EntityAccess.getRelatedEntityChildren(entityClass);
        this.allEntityFields = EntityAccess.getAllEntityFields(entityClass, null);
        this.entityFields = EntityAccess.getEntityFields(entityClass);
    }

    /**
     * @return the entity class
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * @return the related entity classes
     */
    public Class<?>[] getRelatedEntityClasses() {
        return relatedEntityClasses;
    }

    /**
     * @return the related entity children
     */
    public EntityChild[] getRelatedEntityChildren() {
        return relatedEntityChildren;
    }

    /**
     * @return the all entity fields
     */
    public EntityField[] getAllEntityFields() {
        return allEntityFields;
    }

    /**
     * @return the entity fields
     */
    public EntityField[] getEntityFields() {
        return entityFields;
    }

    public String getTableName() {
        return EntityAccess.getTableName(this.relatedEntityClasses[0]);
    }
}
