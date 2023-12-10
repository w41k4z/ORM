import javax.swing.text.html.parser.Entity;

import movement.EntryMovement;
import proj.w41k4z.orm.database.dialect.PostgreSqlDialect;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;
import proj.w41k4z.orm.spec.EntityManager;

public class Test {
    public static void main(String[] args) throws Exception {
        EntryMovement outflow = new EntryMovement();
        EntityManager<EntryMovement, Integer> entityManager = new EntityManager<>(outflow);
        System.out.println(entityManager.findAll());
    }
}
