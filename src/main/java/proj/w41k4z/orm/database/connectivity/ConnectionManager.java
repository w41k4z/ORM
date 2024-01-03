package proj.w41k4z.orm.database.connectivity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.DataSource;

public class ConnectionManager {

    private static Map<String, HikariDataSource> dataSources = new HashMap<>();
    private static Map<String, DataSource> complementaryDataSources;

    static {
        complementaryDataSources = OrmConfiguration.getConnectionPoolingConfiguration().initialize();
        for (Map.Entry<String, DataSource> entry : complementaryDataSources.entrySet()) {
            createDatabaseConnection(entry.getKey(), entry.getValue());
        }
    }

    public static DatabaseConnection getDatabaseConnection(String connectionName) throws SQLException {
        if (dataSources.get(connectionName) != null) {
            return new DatabaseConnection(connectionName, complementaryDataSources.get(connectionName),
                    dataSources.get(connectionName).getConnection());
        }
        throw new RuntimeException("Connection not found. Name: " + connectionName + ".");
    }

    public static DatabaseConnection getDatabaseConnection() throws SQLException {
        return getDatabaseConnection("default");
    }

    public static void createDatabaseConnection(String connectionName, DataSource dataSource) {
        complementaryDataSources.put(connectionName, dataSource);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dataSource.getUrl());
        config.setUsername(dataSource.getUserName());
        config.setPassword(dataSource.getPassword());
        config.setAutoCommit(false);
        config.setMinimumIdle(dataSource.getMinIdle());
        config.setMaximumPoolSize(dataSource.getMaxTotal());
        dataSources.put(connectionName, new HikariDataSource(config));
    }
}