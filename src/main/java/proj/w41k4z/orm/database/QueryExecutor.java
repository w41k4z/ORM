package proj.w41k4z.orm.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {

    public Object executeRequest(String request, Statement statement) throws SQLException {
        if (request.startsWith("SELECT")) {
            return this.executeDQRequest(request, statement);
        } else {
            return this.executeDMRequest(request, statement);
        }
    }

    private Integer executeDMRequest(String request, Statement statement) throws SQLException {
        return statement.executeUpdate(request);
    }

    private ResultSet executeDQRequest(String request, Statement statement) throws SQLException {
        return statement.executeQuery(request);
    }
}
