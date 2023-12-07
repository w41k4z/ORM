package proj.w41k4z.orm.spec;

import java.lang.reflect.Field;

/**
 * {@code EntityField} is a class used to store information about an entity
 * field.
 */
public class EntityField {

    private Field field;
    private Class<?> entityClass;
    private String fieldName;
    private String columnName;

    /**
     * Default constructor, only used by the {@code EntityManager} class (Important
     * checks are already handled from there).
     * 
     * @param field       the entity field
     * @param entityClass the entity class
     */
    protected EntityField(Field field, Class<?> entityClass) {
        this.field = field;
        this.entityClass = entityClass;
        this.fieldName = field.getName();
        this.columnName = EntityManager.getEntityColumnName(field);
    }

    /**
     * Get the field
     * 
     * @return the field
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Get the entity class
     * 
     * @return the entity class
     */
    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    /**
     * Get the field name
     * 
     * @return the field name
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Get the column name
     * 
     * @return the column name
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Get the table name of this field
     * 
     * @return the table name
     */
    public String getTableName() {
        return EntityManager.getEntityTableName(entityClass);
    }

    /**
     * Get the column name with the entity name.
     * Like this: entity1.column1
     * 
     * @return the full column name
     */
    public String getFullColumnName() {
        String tableName = this.getTableName();
        return tableName.concat(".").concat(this.columnName).concat(" " + this.columnName + "__of__" + tableName);
    }
}
