package proj.w41k4z.orm.spec;

import java.lang.reflect.Field;
import proj.w41k4z.orm.annotation.relationship.Key;

/**
 * This class is a representation of an entity child (with relationship).
 */
public class EntityChild {

    private Field field;
    private EntityField[] entityFields;
    private Class<?>[] entityTableClasses;

    /**
     * Default constructor. This is meant to be used by the EntityManager (which
     * have some control) so it can not be instatiated from else where
     * 
     * @param field the entity field
     */
    protected EntityChild(Field field) {
        if (!field.isAnnotationPresent(Key.class)) {
            throw new IllegalArgumentException(
                    "The annotation @Key is missing for this child relationship. Do not forget it");
        }
        this.field = field;
        Class<?> entityClass = field.getType().isArray() ? field.getType().getComponentType() : field.getType();
        this.entityFields = EntityManager.getEntityColumns(entityClass, null);
        this.entityTableClasses = EntityManager.getRelatedEntityTableClasses(entityClass);
    }

    /**
     * This method returns the entity class related to this entity child
     * 
     * @return the child entity class
     */
    public Class<?> getEntityClass() {
        return this.field.getType().isArray() ? this.field.getType().getComponentType() : this.field.getType();
    }

    /**
     * This method returns the child as a Field
     * 
     * @return the field
     */
    public Field getField() {
        return field;
    }
}
