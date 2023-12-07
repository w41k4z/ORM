import movement.EntryMovement;
import movement.OutflowMovement;
import movement.PendingStockEntryMovement;
import movement.StockMovement;
import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.annotation.relationship.OneToOne;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;

public class Test {
    public static void main(String[] args) {
        OQL test = new OQL(QueryType.GET, PendingStockEntryMovement.class);
        System.out.println(test.toNativeQuery());
    }
}
