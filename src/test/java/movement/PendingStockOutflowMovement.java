package movement;

import java.util.List;

import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.Column;

@Entity
// @DiscriminatorValue("OUT")
public class PendingStockOutflowMovement extends PendingMovement {

    public List<OutflowMovement> pendingStockOutflowBreakdown() {
        return null;
    }
}
