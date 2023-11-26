package proj.w41k4z.orm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private DataSource dataSource;
    private Dialect dialect;
    private Connection connection;

    public DatabaseConnection(DataSource dataSource, Dialect dialect) throws SQLException {
        this.dataSource = dataSource;
        this.dialect = dialect;
        this.connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUserName(),
                dataSource.getPassword());
    }

    /**
     * This method is used to get the DataSource of the connection.
     * 
     * @return The URL of the database
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * This method is used to get the Dialect of the connection.
     * 
     * @return The Dialect of the connection
     */
    public Dialect getDialect() {
        return dialect;
    }

    /**
     * This method is used to get the Connection object.
     * 
     * @return The Connection object
     */

    public Connection getConnection() {
        return connection;
    }
}
