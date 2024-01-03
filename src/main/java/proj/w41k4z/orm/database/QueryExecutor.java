package proj.w41k4z.orm.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * {@code QueryExecutor} is a class used to execute a request.
 * Please, read carefully its documentation.
 */
public class QueryExecutor {

    /**
     * Execute a request.
     * 
     * @param request    The request to execute
     * @param connection The connection to use
     * @return The result of the request
     * @throws SQLException If the request is invalid
     */
    public Object executeRequest(String request, Connection connection) throws SQLException {
        if (request.startsWith("SELECT")) {
            return this.executeDQRequest(request, connection);
        } else {
            return this.executeDMRequest(request, connection);
        }
    }

    /**
     * Execute a DML request.
     * 
     * @param request    The request to execute
     * @param connection The connection to use
     * @return An array of Integer. The [0] is the number of rows affected and the
     *         [1] is the generated key (if any)
     * @throws SQLException If the request is invalid
     */
    private Integer[] executeDMRequest(String request, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
        Integer[] results = new Integer[2];
        results[0] = statement.executeUpdate();
        Integer generatedKey = -1;
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            generatedKey = generatedKeys.getInt(1);
        }
        results[1] = generatedKey;
        return results;
    }

    /**
     * Execute a DQ request.
     * 
     * @param request   The request to execute
     * @param statement
     * @return
     * @throws SQLException
     */
    private ResultSet executeDQRequest(String request, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(request);
    }
}
