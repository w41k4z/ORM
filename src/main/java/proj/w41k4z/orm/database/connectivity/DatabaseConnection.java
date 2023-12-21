package proj.w41k4z.orm.database.connectivity;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import proj.w41k4z.orm.database.DataSource;

/**
 * This class is used by the {@link proj.w41k4z.orm.spec.EntityManager} to
 * communicate with the database.
 * 
 */
public class DatabaseConnection {

    private String connectionName;
    private DataSource dataSource;
    private Connection connection;
    private BasicDataSource basicDataSource;

    /**
     * Constructor with all the parameters
     * 
     * @param dataSource     the DataSource of the connection
     * @param connectionName the name of the connection
     * @param minIdle        the minimum number of idle connections in the pool
     * @param maxIdle        the maximum number of idle connections in the pool
     * @param maxTotal       the maximum number of connections in the pool
     * @throws SQLException if the connection to the database failed
     */
    protected DatabaseConnection(String connectionName, DataSource dataSource, int minIdle, int maxIdle, int maxTotal)
            throws SQLException {
        this.connectionName = connectionName;
        this.dataSource = dataSource;
        this.basicDataSource = new BasicDataSource();
        this.basicDataSource.setUrl(dataSource.getUrl());
        this.basicDataSource.setUsername(dataSource.getUserName());
        this.basicDataSource.setPassword(dataSource.getPassword());
        this.basicDataSource.setMinIdle(minIdle);
        this.basicDataSource.setMaxIdle(maxIdle);
        this.basicDataSource.setMaxTotal(maxTotal);
    }

    /**
     * Default constructor with connection name as "default".
     * This class can not be instantiated except by the
     * {@link proj.w41k4z.orm.database.connectivity.ConnectionManager} as it also
     * creates the connection pool.
     * 
     * @param dataSource the DataSource of the connection
     * @throws SQLException if the connection to the database failed
     */
    protected DatabaseConnection(DataSource dataSource) throws SQLException {
        this("default", dataSource);
    }

    /**
     * Constructor with a connection name
     * 
     * @param connectionName the name of the connection
     * @param dataSource     the DataSource of the connection
     * @throws SQLException if the connection to the database failed
     */
    protected DatabaseConnection(String connectionName, DataSource dataSource) throws SQLException {
        this(connectionName, dataSource, 1, 2, 3);
    }

    /**
     * This method is used to get the connection name.
     * 
     * @return String The connection name
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * This method
     * 
     * @return The DataSource object
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * This method is used to get the Connection object.
     * If the current connection of this DatabaseConnection object is closed or
     * null, a new connection is created from its BasicDataSource.
     * 
     * @return The Connection object
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            this.connection = basicDataSource.getConnection();
        }
        return this.connection;
    }

    public void commit() throws SQLException {
        this.connection.commit();
    }

    public void rollback() throws SQLException {
        this.connection.rollback();
    }

    /**
     * This method close the connection. Be aware that all the connection are pooled
     * so any further call to getConnection() after a connection closure will return
     * a new connection.
     */
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            // Closing a connection is not a critical operation
        }
    }
}
