package proj.w41k4z.orm.database.connectivity;

import java.sql.SQLException;
import java.util.Map;

import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.DataSource;

public class ConnectionManager {

    private static Map<String, DatabaseConnection> dataSources;

    static {
        dataSources = OrmConfiguration.getConnectionPoolingConfiguration().initialize();
    }

    public static DatabaseConnection getDatabaseConnection(String connectionName) throws SQLException {
        DatabaseConnection databaseConnection = dataSources.get(connectionName);
        // databaseConnection.retrieveConnection();
        return databaseConnection;
    }

    public static DatabaseConnection getDatabaseConnection() throws SQLException {
        return getDatabaseConnection("default");
    }

    public static void createDatabaseConnection(String connectionName, DataSource dataSource, int minIdle,
            int maxTotal) throws SQLException {
        dataSources.put(connectionName, new DatabaseConnection(connectionName, dataSource, minIdle, maxTotal));
    }
}