package proj.w41k4z.orm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is meant to be used by the {@link EntityManager} to communicate
 * with the database.
 */
public class DatabaseConnection {

    private String connectionName;
    private DataSource dataSource;
    private Connection connection;

    /**
     * Default constructor with connection name as "default"
     * 
     * @param dataSource the DataSource of the connection
     * @throws SQLException if the connection to the database failed
     */
    public DatabaseConnection(DataSource dataSource) throws SQLException {
        this(dataSource, "default");
    }

    /**
     * Constructor with a connection name
     * 
     * @param dataSource     the DataSource of the connection
     * @param connectionName the name of the connection
     * @throws SQLException if the connection to the database failed
     */
    public DatabaseConnection(DataSource dataSource, String connectionName) throws SQLException {
        this.connectionName = connectionName;
        this.dataSource = dataSource;
        this.connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUserName(),
                dataSource.getPassword());
        this.connection.setAutoCommit(false);
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
     * @return Connection The Connection object
     */
    public Connection getConnection() {
        return connection;
    }
}
