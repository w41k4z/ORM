import movement.EntryMovement;
import movement.OutflowMovement;
import proj.w41k4z.orm.database.dialect.PostgreSqlDialect;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;

public class Test {
    public static void main(String[] args) throws Exception {
        OutflowMovement outflow = new OutflowMovement();
        // outflow.setId(Long.valueOf(0));
        OQL test = new OQL(QueryType.REMOVE, outflow, new PostgreSqlDialect());
        System.out.println(test.toNativeQuery().getRequest());
    }
}
