package proj.w41k4z.orm.spec;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import proj.w41k4z.orm.DataAccessObject;
import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.DatabaseConnection;
import proj.w41k4z.orm.database.QueryExecutor;
import proj.w41k4z.orm.database.Transaction;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;
import proj.w41k4z.orm.database.request.NativeQueryBuilder;

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
 * 
 * Note: Do not forget to commit or rollback and close the transaction
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

    /**
     * Fetch all the current entity from databasae.
     * 
     * @return all the fetched entity
     * @throws NoSuchMethodException     if one of the entity field setter is not
     *                                   found
     * @throws IllegalAccessException    if one of the entity field setter is not
     *                                   accessible
     * @throws IllegalArgumentException  if one of the setter parameter type does
     *                                   not match the field value type
     * @throws InvocationTargetException if one of the setter cannot be invoked
     * @throws InstantiationException    if the entity cannot be instantiated
     * @throws SecurityException         if one of the setter is not accessible
     * @throws SQLException              if the request fails
     */
    @Override
    @SuppressWarnings("unchecked")
    public E[] findAll() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, SecurityException, SQLException {
        if (this.entity == null) {
            throw new NullPointerException("The entity is not set");
        }
        DatabaseConnection databaseConnection = this.transaction.getCurrentDatabaseConnection();
        OQL objectQueryLanguage = new OQL(QueryType.GET, this.entity, databaseConnection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        QueryExecutor queryExecutor = new QueryExecutor();
        Object[] result = EntityMapping.map(
                (ResultSet) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                        databaseConnection.getConnection()),
                this.entity.getClass());
        E[] entities = (E[]) Array.newInstance(this.entity.getClass(), result.length);
        for (int i = 0; i < result.length; i++) {
            entities[i] = (E) result[i];
        }
        return entities;
    }

    /**
     * 
     * @param connectionName the connection name to use
     * @return all the fetched entity
     * @throws NoSuchMethodException     if one of the entity field setter is not
     *                                   found
     * @throws IllegalAccessException    if one of the entity field setter is not
     *                                   accessible
     * @throws IllegalArgumentException  if one of the setter parameter type does
     *                                   not match the field value type
     * @throws InvocationTargetException if one of the setter cannot be invoked
     * @throws InstantiationException    if the entity cannot be instantiated
     * @throws SecurityException         if one of the setter is not accessible
     * @throws SQLException              if the request fails
     */
    public E[] findAll(String connectionName)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, SecurityException, SQLException {
        this.transaction.use(connectionName);
        return this.findAll();
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
