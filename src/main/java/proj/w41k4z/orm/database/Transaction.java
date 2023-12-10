package proj.w41k4z.orm.database;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * {@code Transaction} is class used to manage transactions.
 * It supports multiple database connections and cab handle multiple
 * transactions between each of them.
 * Note that the first database connection added is named "default".
 */
public class Transaction {

    private HashMap<String, DatabaseConnection> databaseConnections;
    private HashMap<String, Boolean> databaseConnectionsStatus;
    private DatabaseConnection currentDatabaseConnection;

    /**
     * Default constructor
     * 
     * @param databaseConnection the default database connection.
     */
    public Transaction(DatabaseConnection databaseConnection) {
        this.databaseConnections = new HashMap<String, DatabaseConnection>();
        this.addDatabaseConnection(databaseConnection);
        this.start(databaseConnection.getConnectionName());
        this.use(databaseConnection.getConnectionName());
    }

    /**
     * Starts and sets active all the database connections.
     */
    public void start() {
        for (String databaseConnectionName : this.databaseConnections.keySet()) {
            this.start(databaseConnectionName);
        }
    }

    /**
     * Starts and sets active a database connection.
     * 
     * @param name the name of the database connection
     */
    public void start(String name) {
        this.databaseConnectionsStatus.replace(name, true);
    }

    public void use(String name) {
        // checking the database connection status
        if (!this.databaseConnectionsStatus.get(name)) {
            throw new RuntimeException("The connection " + name + " is not active. Do not forget to start it.");
        }
        this.currentDatabaseConnection = this.databaseConnections.get(name);
    }

    /**
     * Adds a database connection to the transaction
     * 
     * @param name               the name of the database connection
     * @param databaseConnection the database connection
     */
    public void addDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnections.put(databaseConnection.getConnectionName(), databaseConnection);
        this.databaseConnectionsStatus.put(databaseConnection.getConnectionName(), false);
    }

    /**
     * Removes a database connection from the transaction. This also close the
     * connection but does not commit its transaction.
     * 
     * @param name the name of the database connection
     * @throws SQLException
     */
    public void removeDatabaseConnection(String name) throws SQLException {
        this.close(name);
        this.databaseConnections.remove(name);
    }

    /**
     * Returns the current database connection
     * 
     * @return the current database connection
     */
    public DatabaseConnection getCurrentDatabaseConnection() {
        return this.currentDatabaseConnection;
    }

    /**
     * Commits all active database connections in the transaction
     * 
     * @throws SQLException
     */
    public void commitAll() throws SQLException {
        for (String databaseConnectionName : this.databaseConnections.keySet()) {
            this.commit(databaseConnectionName);
        }
    }

    /**
     * Commits an active database connection in the transaction
     * 
     * @param name the name of the database connection
     * @throws SQLException
     */
    public void commit(String name) throws SQLException {
        // only commit if the database connection status is active
        if (this.databaseConnectionsStatus.get(name)) {
            this.databaseConnections.get(name).getConnection().commit();
        }
    }

    /**
     * Rollbacks all active database connections in the transaction
     * 
     * @throws SQLException
     */
    public void rollbackAll() throws SQLException {
        for (String databaseConnectionName : this.databaseConnections.keySet()) {
            this.rollback(databaseConnectionName);
        }
    }

    /**
     * Rollbacks an active database connection in the transaction
     * 
     * @param name the name of the database connection
     * @throws SQLException
     */
    public void rollback(String name) throws SQLException {
        // only rollback if the database connection status is active
        if (this.databaseConnectionsStatus.get(name)) {
            this.databaseConnections.get(name).getConnection().rollback();
        }
    }

    /**
     * Closes all the database connections in the transaction
     * 
     * @throws SQLException
     */
    public void closeAll() throws SQLException {
        for (String databaseConnectionName : this.databaseConnections.keySet()) {
            this.close(databaseConnectionName);
        }
    }

    /**
     * Closes a database connection in the transaction. After a close, the database
     * connection status is set to inactive and cannot be reopened anymore.
     * 
     * @param name the name of the database connection
     * @throws SQLException
     */
    public void close(String name) throws SQLException {
        this.databaseConnectionsStatus.replace(name, false);
        this.databaseConnections.get(name).getConnection().close();
    }
}
