package proj.w41k4z.orm.spec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.Id;
import proj.w41k4z.orm.annotation.relationship.Inheritance;
import proj.w41k4z.orm.annotation.relationship.ManyToMany;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.annotation.relationship.OneToOne;
import proj.w41k4z.orm.enumeration.InheritanceType;

/**
 * {@code EntityAccess} is an abstract class used to access entity information
 * through its useful static methods.
 */
public abstract class EntityAccess {

    /**
     * Tests if the given class is an entity
     * 
     * @param entityClass the class to check
     * @return true if the class is an entity, false otherwise
     */
    public static boolean isEntity(Class<?> entityClass) {
        return entityClass.isAnnotationPresent(Entity.class);
    }

    /**
     * Check if the given class is an entity. If not, throw an
     * IllegalArgumentException.
     * 
     * @param entityClass the class to check
     */
    public static void check(Class<?> entityClass) {
        if (!isEntity(entityClass)) {
            throw new IllegalArgumentException(
                    "The class " + entityClass.getSimpleName()
                            + " is not an entity. Do not forget to annotate it with @Entity.");
        }
    }

    /**
     * Get the entity table name
     * 
     * @param entityClass the entity class
     * @return the entity table name
     */
    public static String getTableName(Class<?> entityClass) {
        check(entityClass);

        return entityClass.getAnnotation(Entity.class).table().equals("") ? entityClass.getSimpleName()
                : entityClass.getAnnotation(Entity.class).table();
    }

    /**
     * Get the entity Id (Inheritance included)
     * 
     * @param entityClass the entity class
     * @return the entity id
     */
    public static EntityField getId(Class<?> entityClass) {
        check(entityClass);

        Field[] ids = JavaClass.getFieldByAnnotation(entityClass, Id.class);
        switch (ids.length) {
            case 0:
                Class<?> superClass = entityClass.getSuperclass();
                if (!superClass.equals(Object.class) && superClass.isAnnotationPresent(Inheritance.class))
                    return getId(superClass);
                else
                    throw new IllegalArgumentException(
                            "The entity " + entityClass.getSimpleName()
                                    + " must have one field annotated with @Id. If it has an entity super class, check the @Id column.");
            case 1:
                if (ids[0].isAnnotationPresent(Column.class)) {
                    return new EntityField(ids[0], entityClass);
                }
                throw new UnsupportedOperationException(
                        "An id field annotated with @Id must be annotated with the annotation @Column too. Source: "
                                + entityClass.getSimpleName() + ". Id field: " + ids[0].getName());
            default:
                throw new IllegalArgumentException("The entity " + entityClass.getSimpleName()
                        + " must have only one field annotated with @Id.");
        }
    }

    /**
     * Get the entity columns with their source entity (Inheritance supported).
     * 
     * @param entityClass       the entity class
     * @param columnSourceTable the entity class associated to the column concerned.
     *                          If null, the entityClass will be used instead
     * 
     * @return the entity columns
     */
    public static EntityField[] getColumns(Class<?> entityClass, Class<?> columnSourceTable) {
        check(entityClass);

        // If the entity class has a super class and it is an entity
        if (!entityClass.getSuperclass().equals(Object.class)
                && entityClass.getSuperclass().isAnnotationPresent(Inheritance.class)) {
            EntityField[] superFields = null;
            InheritanceType inheritanceType = entityClass.getSuperclass().getAnnotation(Inheritance.class).type();
            switch (inheritanceType) {
                /*
                 * If the inheritance type is JOINED_TABLE, the super class is the column
                 * source entity of the upper fields because the super class is a standalone
                 * table/entity
                 */
                case JOINED_TABLE:
                    superFields = getColumns(entityClass.getSuperclass(), null);
                    break;
                /*
                 * Otherwise, the current class is the column source entity of the super class
                 */
                default:
                    superFields = getColumns(entityClass.getSuperclass(), entityClass);
                    break;
            }

            /*
             * Column source table of an inheritance type of SAME_TABLE is a bit specific
             * because the entity information is stored in the super class.
             */
            Class<?> currentColumnSourceTable = inheritanceType.equals(InheritanceType.SAME_TABLE)
                    ? entityClass.getSuperclass()
                    : entityClass;
            ArrayList<EntityField> fields = new ArrayList<>();
            Arrays.stream(JavaClass.getFieldByAnnotation(entityClass, Column.class))
                    .forEach(field -> {
                        fields.add(
                                new EntityField(field, currentColumnSourceTable));
                    });
            EntityField[] entityFields = fields.toArray(new EntityField[0]);

            EntityField[] allFields = new EntityField[superFields.length + entityFields.length];
            System.arraycopy(superFields, 0, allFields, 0, superFields.length);
            System.arraycopy(entityFields, 0, allFields, superFields.length, entityFields.length);
            return allFields;
        } else {
            ArrayList<EntityField> entityFields = new ArrayList<>();
            Arrays.stream(JavaClass.getFieldByAnnotation(entityClass, Column.class))
                    .forEach(field -> {
                        entityFields.add(
                                new EntityField(field, columnSourceTable == null ? entityClass : columnSourceTable));
                    });
            return entityFields.toArray(new EntityField[0]);
        }
    }

    /**
     * Get all related children of the given entity class (Inheritance supported).
     * Children are those fields annotated with a relationship annotation such
     * as @OneToOne, @OneToMany, @ManyToMany.
     * 
     * @param entityClass the entity class
     * @return the related children
     */
    public static EntityChild[] getRelatedChildren(Class<?> entityClass) {
        check(entityClass);

        EntityChild[] inheritedEntityChildren = !entityClass.getSuperclass().equals(Object.class)
                && entityClass.getSuperclass().isAnnotationPresent(Inheritance.class)
                        ? getRelatedChildren(entityClass.getSuperclass())
                        : new EntityChild[0];

        Field[] children = JavaClass.getFieldByAnnotation(entityClass, OneToOne.class, OneToMany.class,
                ManyToMany.class);
        EntityChild[] entityChildren = new EntityChild[children.length + inheritedEntityChildren.length];
        for (int i = 0; i < children.length; i++) {
            check(
                    children[i].getType().isArray() ? children[i].getType().getComponentType() : children[i].getType());
            entityChildren[i] = new EntityChild(children[i]);
        }
        for (int i = children.length; i < entityChildren.length; i++) {
            entityChildren[i] = inheritedEntityChildren[i - children.length];
        }
        return entityChildren;
    }

    /**
     * Get all the tables associated to the entity class.
     * This method is meant to return wether an array of one or two elements.
     * Because it mainly concerns the JOINED_TABLE inheritance type.
     * The first element of the array is the main/source table, and the rest is its
     * dependencies (parent table to join...)
     * 
     * @param entityClass the entity class
     * @return
     */
    public static Class<?>[] getRelatedEntityClasses(Class<?> entityClass) {
        check(entityClass);
        ArrayList<Class<?>> entityClasses = new ArrayList<>();

        Class<?> superClass = entityClass.getSuperclass();
        if (superClass != null && superClass.isAnnotationPresent(Inheritance.class)) {
            check(superClass);
            switch (superClass.getAnnotation(Inheritance.class).type()) {
                case DIFFERENT_TABLE:
                    entityClasses.add(entityClass);
                    break;
                case SAME_TABLE:
                    entityClasses.add(superClass);
                    break;
                case JOINED_TABLE:
                    entityClasses.add(entityClass);
                    entityClasses.add(superClass);
                    break;
            }
        } else {
            entityClasses.add(entityClass);
        }

        return entityClasses.toArray(new Class<?>[0]);
    }
}
