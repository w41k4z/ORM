package proj.w41k4z.orm.database;

import java.sql.Connection;

public interface DatabaseConnection extends Connection {

    /**
     * This method is used to get the DataSource of the connection.
     * 
     * @return The URL of the database
     */
    public DataSource getDataSource();
}
