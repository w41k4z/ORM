package proj.w41k4z.orm.database.connectivity;

import java.util.Map;

/**
 * This interface is used to manage the connection pooling configuration.
 */
public interface ConnectionPoolingConfiguration {

    /**
     * This method is used to initialize the connection pool.
     * 
     * @return a Map of the connection names and the DatabaseConnection objects
     */
    public Map<String, DatabaseConnection> initialize();
}
