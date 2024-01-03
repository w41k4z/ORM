package proj.w41k4z.orm.database.connectivity;

import java.util.HashMap;
import java.util.Map;

import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.DataSource;

public class DefaultConnectionPoolingConfiguration implements ConnectionPoolingConfiguration {

    @Override
    public Map<String, DataSource> initialize() {
        HashMap<String, DataSource> dataSources = new HashMap<>();
        try {
            dataSources.put("default", OrmConfiguration.getDataSource());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataSources;
    }

}
