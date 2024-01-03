package proj.w41k4z.orm.spec;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.DataAccessObject;
import proj.w41k4z.orm.annotation.Generated;
import proj.w41k4z.orm.database.QueryExecutor;
import proj.w41k4z.orm.database.Transaction;
import proj.w41k4z.orm.database.connectivity.ConnectionManager;
import proj.w41k4z.orm.database.connectivity.DatabaseConnection;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;
import proj.w41k4z.orm.database.request.Condition;
import proj.w41k4z.orm.database.request.NativeQueryBuilder;
import proj.w41k4z.orm.database.request.Operator;
import proj.w41k4z.orm.enumeration.GenerationType;

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
        this(ConnectionManager.getDatabaseConnection());
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

    @SuppressWarnings("unchecked")
    public E[] findAll(Condition condition)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, SecurityException, SQLException {
        if (this.entity == null) {
            throw new NullPointerException("The entity is not set");
        }
        DatabaseConnection databaseConnection = this.transaction.getCurrentDatabaseConnection();
        OQL objectQueryLanguage = new OQL(QueryType.GET, this.entity, databaseConnection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        nativeQueryBuilder.appendCondition(condition);
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

    public E[] findAll(String connectionName, Condition condition)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, SecurityException, SQLException {
        this.transaction.use(connectionName);
        return this.findAll(condition);
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
    public E[] findAll() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, SecurityException, SQLException {
        return this.findAll(null);
    }

    public E findOne(String connectionName, Condition condition)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException,
            IllegalArgumentException, SecurityException, ClassNotFoundException, SQLException, IOException {
        E[] results = this.findAll(connectionName, condition);
        return results.length > 0 ? results[0] : null;
    }

    public E findOne(Condition condition)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        E[] results = this.findAll(condition);
        return results.length > 0 ? results[0] : null;
    }

    @Override
    public E findById(ID id)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        String tableName = EntityAccess.getTableName(this.entity.getClass());
        String column = EntityAccess.getId(this.entity.getClass(), null).getColumnName() + "__of__" + tableName;
        return this.findOne(Condition.WHERE(column, Operator.E, id));
    }

    public E findById(String connectionName, ID id)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        this.transaction.use(connectionName);
        return this.findById(id);
    }

    @Override
    public Integer create() throws SQLException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, IllegalArgumentException, InstantiationException {
        DatabaseConnection connection = this.transaction.getCurrentDatabaseConnection();
        QueryExecutor queryExecutor = new QueryExecutor();
        EntityField entityId = EntityAccess.getId(this.entity.getClass(), null);
        if (entityId.isGenerated()) {
            // Generation auto type are just ignored
            if (entityId.getField().getAnnotation(Generated.class).type().equals(GenerationType.SEQUENCE)) {
                String sequenceName = entityId.getField().getAnnotation(Generated.class).sequenceName();
                if (sequenceName.equals("")) {
                    throw new IllegalArgumentException(
                            "The sequence name cannot be empty for generation type SEQUENCE. Source: `"
                                    + this.getClass().getSimpleName() + "." + entityId.getField().getName() + "`");
                }
                String idPrefix = entityId.getField().getAnnotation(Generated.class).pkPrefix();
                int idLength = entityId.getField().getAnnotation(Generated.class).pkLength();
                ResultSet result = (ResultSet) queryExecutor.executeRequest(
                        "SELECT " + connection.getDataSource().getDialect().getSequenceNextValString(sequenceName),
                        connection.getConnection());
                result.next();
                String generatedId = result.getString(1);
                StringBuilder idValue = new StringBuilder(idPrefix);
                for (int i = 0; i < idLength - generatedId.length(); i++) {
                    idValue.append("0");
                }
                idValue.append(generatedId);
                JavaClass.setObjectFieldValue(this, idValue.toString(), entityId.getField());
                result.getStatement().close();
                result.close();
            }
        }
        OQL objectQueryLanguage = new OQL(QueryType.ADD, this.entity, connection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        Integer[] results = new Integer[] { -1, -1 };
        try {
            results = (Integer[]) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                    connection.getConnection());
            // A generated id was returned
            if (results[1] != -1) {
                JavaClass.setObjectFieldValue(this.entity, results[1], entityId.getField());
            }
        } catch (SQLException e) {
            if (connection != null && connection.getConnection() != null) {
                this.transaction.rollback(connection.getConnectionName());
            }
        }
        return results[0];
    }

    public Integer create(String connectionName) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, IllegalArgumentException, InstantiationException, SQLException {
        this.transaction.use(connectionName);
        return this.create();
    }

    @Override
    public Integer update() throws SQLException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        DatabaseConnection connection = this.transaction.getCurrentDatabaseConnection();
        OQL objectQueryLanguage = new OQL(QueryType.CHANGE, this, connection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        QueryExecutor queryExecutor = new QueryExecutor();
        Integer[] results = new Integer[] { -1, -1 };
        try {
            results = (Integer[]) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                    connection.getConnection());
        } catch (SQLException e) {
            if (connection != null && connection.getConnection() != null) {
                this.transaction.rollback(connection.getConnectionName());
            }
        }
        return results[0];
    }

    public Integer update(String connectionName) throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SQLException {
        this.transaction.use(connectionName);
        return this.update();
    }

    @Override
    public Integer delete() throws SQLException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        DatabaseConnection connection = this.transaction.getCurrentDatabaseConnection();
        Integer[] results = new Integer[] { -1, -1 };
        OQL objectQueryLanguage = new OQL(QueryType.REMOVE, this, connection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        QueryExecutor queryExecutor = new QueryExecutor();
        try {
            results = (Integer[]) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                    connection.getConnection());
        } catch (SQLException error) {
            if (connection != null && connection.getConnection() != null) {
                this.transaction.rollback(connection.getConnectionName());
            }
        }
        return results[0];
    }

    public Integer delete(String connectionName) throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SQLException {
        this.transaction.use(connectionName);
        return this.delete();
    }
}
