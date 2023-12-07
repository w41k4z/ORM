package movement;

import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.DiscriminatorValue;
import proj.w41k4z.orm.annotation.Entity;

@Entity
@DiscriminatorValue("IN")
public class EntryMovement extends ValidatedStockTransaction {

    @Column(name = "unit_price")
    private Double unitPrice;

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
