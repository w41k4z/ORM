package proj.w41k4z.orm.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * {@code QueryExecutor} is a class used to execute a request.
 * Please, read carefully its documentation.
 */
public class QueryExecutor {

    /**
     * Execute a request. Note: The connection closing is not handled by this
     * method. You have to close it by yourself
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
     * Execute a DML request. Note: This close the statement after the request
     * execution
     * 
     * @param request    The request to execute
     * @param connection The connection to use
     * @return An array of Integer. The [0] is the number of rows affected and the
     *         [1] is the generated key (if any)
     * @throws SQLException If the request is invalid
     */
    private Integer[] executeDMRequest(String request, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        Integer[] results = new Integer[2];
        results[0] = statement.executeUpdate(request);
        Integer generatedKey = -1;
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            generatedKey = generatedKeys.getInt(1);
        }
        results[1] = generatedKey;
        statement.close();
        return results;
    }

    /**
     * Execute a DQ request. Note: This does not close directly the statement as it
     * returns the result set. You have to explicitly close the statement from that
     * returned resultset when using it by yourself (request execution done by this
     * framework is already taking care of that)
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
