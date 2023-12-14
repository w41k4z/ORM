package proj.w41k4z.orm.database.request;

import proj.w41k4z.orm.database.Dialect;

public class NativeQueryBuilder {

    private RequestType type;
    private String table;
    private String[] columns;
    private Object[] columnValues;
    private Dialect dialect;
    private Condition condition;
    private StringBuilder request;

    /**
     * Constructor for raw request
     *
     * @param table      the concerned table (needed when trying to append condition
     *                   to the request)
     * @param rawRequest the raw request
     */
    public NativeQueryBuilder(String table, String rawRequest) {
        this.table = table;
        this.request = new StringBuilder(rawRequest);
    }

    /**
     * Constructor for raw SELECT request with Condition
     * 
     * @param rawRequest the raw request
     * @param condition  the condition to add
     */
    public NativeQueryBuilder(String table, String rawRequest, Condition condition) {
        this(table, rawRequest);
        this.setCondition(condition);
        this.appendCondition(condition);
    }

    /**
     * Constructor for SELECT request.
     * 
     * @param table     the concerned table
     * @param columns   the concerned columns
     * @param dialect   the dialect to use
     * @param condition the condition to add
     */
    public NativeQueryBuilder(String table, String[] columns, Dialect dialect, Condition condition) {
        this.setType(RequestType.SELECT);
        this.setTable(table);
        this.setDialect(dialect);
        this.setCondition(condition);
        this.build();
    }

    /**
     * @return Type return the type
     */
    public RequestType getType() {
        return type;
    }

    /**
     * @param type the type of the request
     */
    public void setType(RequestType type) {
        this.type = type;
    }

    /**
     * @return String return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the concerned table
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return String[] return all the columns
     */
    public String[] getColumns() {
        return columns;
    }

    /**
     * @param columns the concerned columns
     */
    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    /**
     * @return Object[] all the column values
     */
    public Object[] getColumnValues() {
        return columnValues;
    }

    /**
     * @param values the column values
     */
    public void setColumnValues(Object[] values) {
        this.columnValues = values;
    }

    /**
     * @return Dialect return the dialect
     */
    public Dialect getDialect() {
        return dialect;
    }

    /**
     * @param dialect the dialect to use
     */
    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * @return Condition return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to add
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * This method is used the get the built request
     * 
     * @return The build request
     */
    public StringBuilder getRequest() {
        return request;
    }

    public void appendCondition(Condition condition) {
        if (condition != null) {
            StringBuilder request = new StringBuilder(
                    "SELECT * FROM (" + this.getRequest().toString() + ") " + this.table);
            this.request = request.append(condition.getCondition());
        }
    }

    /**
     * This method is called to choose which builder method to be used
     */
    private void build() {
        this.request = new StringBuilder(this.type.toString());
        switch (this.type) {
            case SELECT:
                this.buildSelectRequest();
                break;
            case INSERT:
                this.buildInsertRequest();
                break;
            case UPDATE:
                this.buildUpdateRequest();
                break;
            case DELETE:
                this.buildDeleteRequest();
                break;
        }
    }

    /**
     * This method build a SELECT request according to the parameter passed to the
     * constructor
     */
    private void buildSelectRequest() {

    }

    /**
     * This method build an INSERT request according to the parameter passed to the
     * constructor
     */
    private void buildInsertRequest() {
    }

    /**
     * This method build an UPDATE request according to the parameter passed to the
     * constructor
     */
    private void buildUpdateRequest() {
    }

    /**
     * This method build a DELETE request according to the parameter passed to the
     * constructor
     */
    private void buildDeleteRequest() {
    }
}
