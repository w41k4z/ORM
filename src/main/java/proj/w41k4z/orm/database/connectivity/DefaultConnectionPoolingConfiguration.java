package proj.w41k4z.orm.database.connectivity;

import java.util.HashMap;
import java.util.Map;

import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.DataSource;

public class DefaultConnectionPoolingConfiguration implements ConnectionPoolingConfiguration {

    @Override
    public Map<String, DataSource> initialize() {
        HashMap<String, DataSource> dataSources = new HashMap<>();
        dataSources.put("default", OrmConfiguration.getDataSource());
        return dataSources;
    }

}
