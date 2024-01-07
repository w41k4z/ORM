import proj.w41k4z.orm.database.connectivity.ConnectionManager;
import proj.w41k4z.orm.database.connectivity.DatabaseConnection;

public class Test {
    public static void main(String[] args) throws Exception {
        DatabaseConnection connection = ConnectionManager.getDatabaseConnection();
        System.out.println(connection);
    }
}
