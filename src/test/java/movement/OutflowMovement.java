package movement;

import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.DiscriminatorValue;
import proj.w41k4z.orm.annotation.Entity;

@Entity
@DiscriminatorValue("OUT")
public class OutflowMovement extends ValidatedStockTransaction {

}
