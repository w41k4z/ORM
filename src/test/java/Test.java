import movement.EntryMovement;
import movement.OutflowMovement;
import proj.w41k4z.orm.database.dialect.PostgreSqlDialect;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;

public class Test {
    public static void main(String[] args) throws Exception {
        OQL test = new OQL(QueryType.ADD, new OutflowMovement(), new PostgreSqlDialect());
        System.out.println(test.toNativeQuery().getRequest());
    }
}
