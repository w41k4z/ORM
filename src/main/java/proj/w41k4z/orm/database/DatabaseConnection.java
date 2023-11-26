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
    // public DataSource getDataSource();
}
