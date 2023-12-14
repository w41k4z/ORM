package proj.w41k4z.orm.spec;

import java.lang.reflect.Field;

import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.relationship.Join;
import proj.w41k4z.orm.annotation.relationship.ManyToMany;
import proj.w41k4z.orm.annotation.relationship.ManyToOne;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.annotation.relationship.OneToOne;

/**
 * {@code EntityField} is a class used to store information about an entity
 * field.
 */
public class EntityField {

    private Field field;
    private Class<?> entityClass;

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
     * Get the column name
     * 
     * @return the column name
     */
    public String getColumnName() {
        return getColumnName(this.field);
    }

    /**
     * Get the alias column name
     * 
     * @return the alias column name
     */
    public String getAliasColumnName() {
        String tableName = this.getTableName();
        return getColumnName(this.field) + "__of__" + tableName;
    }

    /**
     * Get the table name of this field
     * 
     * @return the table name
     */
    public String getTableName() {
        return EntityAccess.getTableName(entityClass);
    }

    /**
     * Get the column name with the entity name.
     * Like this: entity1.column1 AS column1__of__entity1
     * 
     * @return the full column name
     */
    public String getFullColumnName() {
        String tableName = this.getTableName();
        String columnName = this.getColumnName();
        return tableName.concat(".").concat(columnName).concat(" AS " + getAliasColumnName());
    }

    /**
     * Get the column name from the given Field.
     * 
     * @param field the field
     * @return the column name
     */
    public static String getColumnName(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            throw new UnsupportedOperationException(
                    "The field `" + field.getName()
                            + "` is not a column. Do not forget to annotate it with @Column. Source: `"
                            + field.getDeclaringClass().getSimpleName() + "`");
        }
        return field.getAnnotation(Column.class).name().equals("") ? field.getName()
                : field.getAnnotation(Column.class).name();
    }

    /**
     * Check if this column field is nullable.
     * 
     * @param field the field
     * @return the column name
     */
    public static boolean isNullable(Field field) {
        // Foreign entity field are nullable
        if (isForeignEntityField(field)) {
            return true;
        }
        if (!field.isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException(
                    "The field `" + field.getName()
                            + "` is not a column. Do not forget to annotate it with @Column. Source: `"
                            + field.getDeclaringClass().getSimpleName() + "`");
        }
        return field.getAnnotation(Column.class).nullable();
    }

    /**
     * Check if this field is a related entity field (entity field that is
     * represented as a foreign column in the database).
     * This mainly concerns field annotated with @OneToOne and @ManyToOne
     * 
     * @param field the field
     * @return true if the field is a related entity field, false otherwise
     */
    public static boolean isRelatedEntityField(Field field) {
        if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
            if (!field.isAnnotationPresent(Column.class)) {
                throw new UnsupportedOperationException("The field `" + field.getName()
                        + "` is missing the @Column annotation. Source: `" + field.getDeclaringClass().getSimpleName()
                        + "`");
            }
            return true;
        }
        return false;
    }

    /**
     * Check if this field is a foreign entity field (entity field that is
     * from a foreign entity having the declaring entity of the given field the
     * entity id as a foreign column).
     * This mainly concerns field annotated with @OneToMany and @ManyToMany.
     * 
     * @param field the field
     * @return true if the field is a foreign entity field, false otherwise
     */
    public static boolean isForeignEntityField(Field field) {
        if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
            if (!field.isAnnotationPresent(Join.class)) {
                throw new UnsupportedOperationException("The field `" + field.getName()
                        + "` is missing the @Join annotation. Source: `" + field.getDeclaringClass().getSimpleName()
                        + "`");
            }
            return true;
        }
        return false;
    }
}
