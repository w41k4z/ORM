package proj.w41k4z.orm.database.request;

/**
 * An enumeration of all available condition operator
 */
public enum Operator {
    E(" = '{value}'"),
    C(" LIKE '%{value}%'"),
    STW(" LIKE '{value}%'"),
    ENW(" LIKE '%{value}'"),
    G(" > {value}"),
    GE(" >= {value}"),
    L(" < {value}"),
    LE(" <= {value}");

    public final String value;

    private Operator(String value) {
        this.value = value;
    }
}
