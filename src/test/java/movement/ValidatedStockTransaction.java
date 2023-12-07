package movement;

import proj.w41k4z.orm.annotation.DiscriminatorColumn;
import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.Id;
import proj.w41k4z.orm.annotation.relationship.Inheritance;
import proj.w41k4z.orm.enumeration.InheritanceType;

@Entity(table = "validated_stock_transaction")
@Inheritance(type = InheritanceType.SAME_TABLE)
@DiscriminatorColumn("action_type")
public abstract class ValidatedStockTransaction extends StockMovement {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long stockMovementId) {
        this.id = stockMovementId;
    }
}
