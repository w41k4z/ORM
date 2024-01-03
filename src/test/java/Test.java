import java.sql.SQLException;

import proj.w41k4z.orm.database.connectivity.ConnectionManager;
import proj.w41k4z.orm.database.connectivity.ConnectionPoolingConfiguration;
import proj.w41k4z.orm.database.connectivity.DatabaseConnection;
import proj.w41k4z.orm.database.connectivity.DefaultConnectionPoolingConfiguration;

public class Test {
    public static void main(String[] args) throws Exception {
        DatabaseConnection connection = ConnectionManager.getDatabaseConnection();
        connection.commit();
        connection.close();
        // connection.retrieveConnection();
        // DatabaseConnection connection2 = ConnectionManager.getDatabaseConnection();
        connection.commit();
        connection.close();
    }
}
