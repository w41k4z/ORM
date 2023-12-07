package proj.w41k4z.orm.spec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.rmi.UnexpectedException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.DataAccessObject;
import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.Id;
import proj.w41k4z.orm.annotation.relationship.Inheritance;
import proj.w41k4z.orm.annotation.relationship.ManyToMany;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.annotation.relationship.OneToOne;
import proj.w41k4z.orm.database.DatabaseConnection;
import proj.w41k4z.orm.database.Transaction;
import proj.w41k4z.orm.enumeration.InheritanceType;
import proj.w41k4z.orm.annotation.Column;

/**
 * {@code EntityManager} is a class containing some useful static methods to
 * deal with entities. It also manages entity persistence and transactions.
 * For a general usage, do not specify the Entity type and the ID type, use
 * Object instead.
 * 
 * - Like this:
 * 
 * <pre>
 * {@code
 * EntityManager<Object, Object> entityManager = new EntityManager<>();
 * }
 * </pre>
 */
public class EntityManager<E, ID> implements DataAccessObject<E, ID> {

    private E entity;
    private Transaction transaction;

    /**
     * Constructor with database connection
     * 
     * @param databaseConnection the database connection
     */
    public EntityManager(DatabaseConnection databaseConnection) {
        this.transaction = new Transaction(databaseConnection);
    }

    /**
     * Default constructor
     * 
     * @throws IOException               if the orm.properties configuration file is
     *                                   not found
     * @throws SQLException              if the connection to the database fails
     * @throws InstantiationException    if the DataSource or Dialect cannot be
     *                                   instantiated
     * @throws IllegalAccessException    if the DataSource or Dialect cannot be
     *                                   instantiated
     * @throws InvocationTargetException if the DataSource or Dialect cannot be
     *                                   instantiated
     * @throws NoSuchMethodException     if the DataSource or Dialect cannot be
     *                                   instantiated
     * @throws ClassNotFoundException    if the DataSource or Dialect class cannot
     *                                   be found
     */
    public EntityManager()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, SQLException, IOException {
        this(new DatabaseConnection(OrmConfiguration.getDataSource()));
    }

    /**
     * Default constructor with entity
     * 
     * @param entity the entity
     * @throws IOException               if the orm.properties configuration file is
     * @throws SQLException              if the connection to the database fails
     * @throws InstantiationException    if the DataSource or Dialect cannot be
     * @throws IllegalAccessException    if the DataSource or Dialect cannot be
     * @throws InvocationTargetException if the DataSource or Dialect cannot be
     * @throws NoSuchMethodException     if the DataSource or Dialect cannot be
     * @throws ClassNotFoundException    if the DataSource or Dialect class cannot
     *                                   be
     */
    public EntityManager(E entity) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, SQLException, IOException {
        this();
        this.setEntity(entity);
    }

    /**
     * Constructor with entity and database connection
     * 
     * @param entity             the entity
     * @param databaseConnection the database connection
     */
    public EntityManager(E entity, DatabaseConnection databaseConnection) {
        this(databaseConnection);
        this.setEntity(entity);
    }

    /**
     * Replace the active entity
     * 
     * @param entity the entity to set
     */
    public void setEntity(E entity) {
        checkEntity(entity.getClass());
        this.entity = entity;
    }

    /**
     * Check if the given class is an entity
     * 
     * @param entityClass the class to check
     * @return true if the class is an entity, false otherwise
     */
    public static boolean isEntity(Class<?> entityClass) {
        return entityClass.isAnnotationPresent(Entity.class);
    }

    private static void checkEntity(Class<?> entityClass) {
        if (!isEntity(entityClass)) {
            throw new IllegalArgumentException(
                    "The given class is not an entity. Do not forget to annotate it with @Entity.");
        }
    }

    public static EntityField getEntityId(Class<?> entityClass) {
        checkEntity(entityClass);

        Field[] ids = JavaClass.getFieldByAnnotation(entityClass, Id.class);
        switch (ids.length) {
            case 0:
                Class<?> superClass = entityClass.getSuperclass();
                if (!superClass.equals(Object.class) && superClass.isAnnotationPresent(Inheritance.class))
                    return getEntityId(superClass);
                else
                    throw new IllegalArgumentException(
                            "The entity class must have one field annotated with @Id. If it has a super class, check the @Id column.");
            case 1:
                if (ids[0].isAnnotationPresent(Column.class)) {
                    return new EntityField(ids[0], entityClass);
                }
                throw new UnsupportedOperationException(
                        "The field annotated with @Id must be annotated with the annotation @Column too.");
            default:
                throw new IllegalArgumentException("The entity class must have only one field annotated with @Id.");
        }
    }

    /**
     * Get the entity columns including the super class columns.
     * 
     * @param entityClass       the entity class
     * @param columnSourceTable the entity class associated to the column. If null,
     *                          the entityClass will be used instead
     * 
     * @return the entity columns
     */
    public static EntityField[] getEntityColumns(Class<?> entityClass, Class<?> columnSourceTable) {
        checkEntity(entityClass);

        if (!entityClass.getSuperclass().equals(Object.class)
                && entityClass.getSuperclass().isAnnotationPresent(Inheritance.class)) {
            EntityField[] superFields = null;
            InheritanceType inheritanceType = entityClass.getSuperclass().getAnnotation(Inheritance.class).type();
            switch (inheritanceType) {
                case JOINED_TABLE:
                    superFields = getEntityColumns(entityClass.getSuperclass(), null);
                    break;
                default:
                    superFields = getEntityColumns(entityClass.getSuperclass(), entityClass);
                    break;
            }

            /*
             * Column source table of an inheritance type of SAME_TABLE is a bit specific
             */
            Class<?> currentColumnSourceTable = inheritanceType.equals(InheritanceType.SAME_TABLE)
                    ? entityClass.getSuperclass()
                    : entityClass;
            ArrayList<EntityField> entityFields = new ArrayList<>();
            Arrays.stream(JavaClass.getFieldByAnnotation(entityClass, Column.class))
                    .forEach(field -> {
                        entityFields.add(
                                new EntityField(field, currentColumnSourceTable));
                    });
            EntityField[] fields = entityFields.toArray(new EntityField[0]);

            EntityField[] allFields = new EntityField[superFields.length + fields.length];
            System.arraycopy(superFields, 0, allFields, 0, superFields.length);
            System.arraycopy(fields, 0, allFields, superFields.length, fields.length);
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

    public static String getEntityColumnName(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException("This field is not a column. Do not forget to annotate it with @Column");
        }
        return field.getAnnotation(Column.class).name().equals("") ? field.getName()
                : field.getAnnotation(Column.class).name();
    }

    public static EntityChild[] getEntityChild(Class<?> entityClass) {
        checkEntity(entityClass);

        EntityChild[] inheritedEntityChildren = !entityClass.getSuperclass().equals(Object.class)
                && entityClass.getSuperclass().isAnnotationPresent(Inheritance.class)
                        ? getEntityChild(entityClass.getSuperclass())
                        : new EntityChild[0];

        Field[] children = JavaClass.getFieldByAnnotation(entityClass, OneToOne.class, OneToMany.class,
                ManyToMany.class);
        EntityChild[] entityChildren = new EntityChild[children.length + inheritedEntityChildren.length];
        for (int i = 0; i < children.length; i++) {
            checkEntity(
                    children[i].getType().isArray() ? children[i].getType().getComponentType() : children[i].getType());
            entityChildren[i] = new EntityChild(children[i]);
        }
        for (int i = children.length; i < entityChildren.length; i++) {
            entityChildren[i] = inheritedEntityChildren[i - children.length];
        }
        return entityChildren;
    }

    public static String getEntityTableName(Class<?> entityClass) {
        checkEntity(entityClass);

        return entityClass.getAnnotation(Entity.class).table().equals("") ? entityClass.getSimpleName()
                : entityClass.getAnnotation(Entity.class).table();
    }

    /**
     * Get all the tables associated to the entity class.
     * This method is meant to return wether an array of one or two elements.
     * Because it only concerns the JOINED_TABLE inheritance type.
     * The first element of the array is the main/source table, and the rest is its
     * dependencies (parent table to join...)
     * 
     * @param entityClass the entity class
     * @return
     */
    public static Class<?>[] getRelatedEntityTableClasses(Class<?> entityClass) {
        checkEntity(entityClass);
        ArrayList<Class<?>> tables = new ArrayList<>();

        Class<?> superClass = entityClass.getSuperclass();
        if (superClass != null && superClass.isAnnotationPresent(Inheritance.class)) {
            checkEntity(superClass);
            switch (superClass.getAnnotation(Inheritance.class).type()) {
                case DIFFERENT_TABLE:
                    tables.add(entityClass);
                    break;
                case SAME_TABLE:
                    tables.add(superClass);
                    break;
                case JOINED_TABLE:
                    tables.add(entityClass);
                    tables.add(superClass);
                    break;
            }
        } else {
            tables.add(entityClass);
        }

        return tables.toArray(new Class<?>[0]);
    }

    /**
     * Get the transaction of the entity manager
     * 
     * @return the transaction
     */
    public Transaction getTransaction() {
        return this.transaction;
    }

    @Override
    public E[] findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public E findById(ID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public E create() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public E update() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public E delete() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
