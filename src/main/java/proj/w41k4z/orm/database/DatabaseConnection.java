package proj.w41k4z.orm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private DataSource dataSource;
    private Connection connection;

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
