package proj.w41k4z.orm.spec;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import proj.w41k4z.orm.DataAccessObject;
import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.DatabaseConnection;
import proj.w41k4z.orm.database.Transaction;

/**
 * The {@code EntityManager} is used to manage entity persistence and
 * transactions.
 * For a general usage, do not specify the Entity type and the ID type, use
 * Object instead.
 * 
 * 
 * Like this:
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
        EntityAccess.check(entity.getClass());
        this.entity = entity;
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
