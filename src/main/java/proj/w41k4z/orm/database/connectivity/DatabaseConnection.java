package proj.w41k4z.orm.database.connectivity;

import java.sql.Connection;
import java.sql.SQLException;

import proj.w41k4z.orm.database.DataSource;

/**
 * This class is used by the
 * {@link proj.w41k4z.orm.database.connectivity.ConnectionManager} to
 * communicate with the database.
 * 
 */
public class DatabaseConnection {

    private String connectionName;
    private DataSource dataSource;
    private Connection connection;

    /**
     * This class can not be instantiated to avoid developers to create their own
     * DatabasaConnection and crash the application. Everything is managed by the
     * ConnectionManage.
     * 
     * @param connectionName the name of the connection
     * @param dataSource     the DataSource of the connection
     * @param connection     the Connection object
     */
    protected DatabaseConnection(String connectionName, DataSource dataSource, Connection connection) {
        this.connectionName = connectionName;
        this.dataSource = dataSource;
        this.connection = connection;
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
     * 
     * @return The Connection object
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    public void commit() throws SQLException {
        this.connection.commit();
    }

    public void rollback() throws SQLException {
        this.connection.rollback();
    }

    /**
     * This method returns the connection to the pool.
     */
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            // Closing a connection is not a critical operation
        }
    }
}
