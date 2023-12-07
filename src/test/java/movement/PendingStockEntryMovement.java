package movement;

import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.DiscriminatorValue;

@Entity
@DiscriminatorValue("IN")
public class PendingStockEntryMovement extends PendingMovement {

    @Column(name = "unit_price")
    private Double unitPrice;

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

}
