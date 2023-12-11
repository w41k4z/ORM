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
import proj.w41k4z.orm.annotation.relationship.ManyToOne;
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
                    "The class `" + entityClass.getSimpleName()
                            + "` is not an entity. Do not forget to annotate it with @Entity.");
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
     * Get the entity Id (Inheritance supported)
     * 
     * @param entityClass       the entity class
     * @param sourceEntityClass the entity class associated to the field concerned.
     *                          If null, the entityClass will be
     *                          used instead.
     * @return the entity id
     */
    public static EntityField getId(Class<?> entityClass, Class<?> sourceEntityClass) {
        check(entityClass);

        Field[] ids = JavaClass.getFieldByAnnotation(entityClass, Id.class);
        switch (ids.length) {
            case 0:
                Class<?> superClass = entityClass.getSuperclass();
                if (!superClass.equals(Object.class) && superClass.isAnnotationPresent(Inheritance.class)) {
                    InheritanceType inheritanceType = entityClass.getSuperclass().getAnnotation(Inheritance.class)
                            .type();
                    switch (inheritanceType) {
                        /*
                         * If the inheritance type is JOINED_TABLE, the super class is the column
                         * source entity of the upper fields because the super class is a standalone
                         * table/entity
                         */
                        case JOINED_TABLE:
                            return getId(superClass, null);
                        /*
                         * Otherwise, the current class is the column source entity of the super class
                         */
                        default:
                            return getId(superClass, entityClass);
                    }
                } else {
                    throw new IllegalArgumentException(
                            "The entity `" + entityClass.getSimpleName()
                                    + "` must have one field annotated with @Id. If it has an entity super class, check the @Id column.");
                }
            case 1:
                if (ids[0].isAnnotationPresent(Column.class)) {
                    return new EntityField(ids[0], sourceEntityClass == null ? entityClass : sourceEntityClass);
                }
                throw new UnsupportedOperationException(
                        "An id field annotated with @Id must be annotated with the annotation @Column too. Source: `"
                                + entityClass.getSimpleName() + "`. Id field: `" + ids[0].getName() + "`.");
            default:
                throw new IllegalArgumentException("The entity `" + entityClass.getSimpleName()
                        + "` must have only one field annotated with @Id.");
        }
    }

    /**
     * Get all the entity column fields with their source entity, with inherited
     * fields. This exclude fields with a relationship annotation (Only those
     * annotated with @Column are taken).
     * 
     * @param entityClass  the entity class
     * @param sourceEntity the entity class associated to the field concerned.
     *                     If null, the entityClass will be used instead
     * 
     * @return the entity column fields
     */
    public static EntityField[] getAllEntityFields(Class<?> entityClass, Class<?> sourceEntity) {
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
                    superFields = getAllEntityFields(entityClass.getSuperclass(), null);
                    break;
                /*
                 * Otherwise, the current class is the column source entity of the super class
                 */
                default:
                    superFields = getAllEntityFields(entityClass.getSuperclass(), entityClass);
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
            Arrays.stream(JavaClass.getFieldByAnnotation(entityClass, Id.class, Column.class))
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
            Arrays.stream(JavaClass.getFieldByAnnotation(entityClass, Id.class, Column.class))
                    .forEach(field -> {
                        entityFields.add(
                                new EntityField(field, sourceEntity == null ? entityClass : sourceEntity));
                    });
            return entityFields.toArray(new EntityField[0]);
        }
    }

    /**
     * Get the entity column fields. The difference with the
     * {@link EntityAccess#getAllEntityFields(Class, Class)
     * getAllEntityFields(Class, Class)} is that it may exclude some inherited
     * column fields according to the inheritance type occuring. And also, this
     * include fields with relationship annotation.
     * To be simple, these are the fields that will be used when inserting or
     * updating an entity.
     * 
     * @param entityClass the entity class
     * 
     * @return the entity column fields
     */
    public static EntityField[] getEntityFields(Class<?> entityClass) {
        check(entityClass);

        Class<?> superClass = entityClass.getSuperclass();
        ArrayList<EntityField> entityFields = new ArrayList<>();
        // Checking the inheritance type in case of a super class
        if (superClass.isAnnotationPresent(Inheritance.class)) {
            InheritanceType inheritanceType = superClass.getAnnotation(Inheritance.class).type();
            // Inherited columns of type JOINED_TABLE are excluded
            if (inheritanceType.equals(InheritanceType.DIFFERENT_TABLE)
                    || inheritanceType.equals(InheritanceType.SAME_TABLE)) {
                EntityField[] inheritedEntityFields = getEntityFields(superClass);
                Arrays.stream(inheritedEntityFields)
                        .forEach(entityField -> {
                            entityFields.add(
                                    new EntityField(entityField.getField(), entityClass));
                        });
            }
        }
        // Only a relationship of type OneToOne or ManyToOne are concerned
        Arrays.stream(
                JavaClass.getFieldByAnnotation(entityClass, Id.class, Column.class, OneToOne.class, ManyToOne.class))
                .forEach(field -> {
                    entityFields.add(
                            new EntityField(field, entityClass));
                });
        return entityFields.toArray(new EntityField[0]);
    }

    /**
     * Get all related children of the given entity class (Inheritance supported).
     * Children are those fields annotated with a relationship annotation such
     * as @OneToOne, @OneToMany, @ManyToMany.
     * 
     * @param entityClass the entity class
     * @return the related children
     */
    public static EntityChild[] getRelatedEntityChildren(Class<?> entityClass) {
        check(entityClass);

        EntityChild[] inheritedEntityChildren = !entityClass.getSuperclass().equals(Object.class)
                && entityClass.getSuperclass().isAnnotationPresent(Inheritance.class)
                        ? getRelatedEntityChildren(entityClass.getSuperclass())
                        : new EntityChild[0];

        /*
         * The rank is used to set the rank of the entity child (Useful when a join from
         * the same entity is occuring)
         */
        int rank = inheritedEntityChildren.length;
        Field[] children = JavaClass.getFieldByAnnotation(entityClass, OneToOne.class, OneToMany.class, ManyToOne.class,
                ManyToMany.class);
        EntityChild[] entityChildren = new EntityChild[children.length + inheritedEntityChildren.length];
        for (int i = 0; i < children.length; i++) {
            check(children[i].getType().isArray() ? children[i].getType().getComponentType() : children[i].getType());
            entityChildren[i] = new EntityChild(children[i], rank++);
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
     * @return the entity classes related to the given entity class
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

    public static EntityMetadata getEntityMetadata(Class<?> entityClass) {
        check(entityClass);

        return new EntityMetadata(entityClass);
    }
}
