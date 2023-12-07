package proj.w41k4z.orm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is meant to be used by the {@link EntityManager} to communicate
 * with the database.
 */
public class DatabaseConnection {

    private DataSource dataSource;
    private Connection connection;

    /**
     * Default constructor
     * 
     * @param dataSource the DataSource of the connection
     * @throws SQLException if the connection to the database failed
     */
    public DatabaseConnection(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUserName(),
                dataSource.getPassword());
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
