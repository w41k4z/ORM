package proj.w41k4z.orm.database.connectivity;

import java.util.HashMap;
import java.util.Map;

import proj.w41k4z.orm.OrmConfiguration;

public class DefaultConnectionPoolingConfiguration implements ConnectionPoolingConfiguration {

    @Override
    public Map<String, DatabaseConnection> initialize() {
        HashMap<String, DatabaseConnection> databaseConnections = new HashMap<>();
        try {
            databaseConnections.put("default", new DatabaseConnection("default", OrmConfiguration.getDataSource()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return databaseConnections;
    }

}
