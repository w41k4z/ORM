package proj.w41k4z.orm.spec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.DataAccessObject;
import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.relationship.ManyToMany;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.annotation.relationship.OneToOne;
import proj.w41k4z.orm.database.DatabaseConnection;
import proj.w41k4z.orm.database.Transaction;
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
        if (!isEntity(entity.getClass())) {
            throw new IllegalArgumentException(
                    "The given parameter is not an entity. Do not forget to annotate it with @Entity.");
        }
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

    /**
     * Get the entity columns including the super class columns.
     * 
     * @param entityClass the entity class
     * @return the entity columns
     */
    public static EntityField[] getEntityColumns(Class<?> entityClass) {
        if (!isEntity(entityClass)) {
            throw new IllegalArgumentException(
                    "The given class is not an entity. Do not forget to annotate it with @Entity.");
        }
        if (entityClass.getSuperclass() != null) {
            EntityField[] superFields = getEntityColumns(entityClass.getSuperclass());

            ArrayList<EntityField> entityFields = new ArrayList<>();
            Arrays.stream(JavaClass.getFieldByAnnotation(entityClass, Column.class))
                    .forEach(field -> {
                        entityFields.add(new EntityField(field, entityClass));
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
                        entityFields.add(new EntityField(field, entityClass));
                    });
            return entityFields.toArray(new EntityField[0]);
        }
    }

    public static Class<?>[] getEntityChild(Class<?> entityClass) {
        if (!isEntity(entityClass)) {
            throw new IllegalArgumentException(
                    "The given class is not an entity. Do not forget to annotate it with @Entity.");
        }
        Field[] children = JavaClass.getFieldByAnnotation(entityClass, OneToOne.class, OneToMany.class,
                ManyToMany.class);
        Class<?>[] entityChildren = new Class<?>[children.length];
        for (int i = 0; i < children.length; i++) {
            entityChildren[i] = children[i].getType();
        }
        return entityChildren;
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
