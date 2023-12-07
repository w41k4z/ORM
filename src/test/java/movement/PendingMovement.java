package movement;

import java.sql.Timestamp;

import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.DiscriminatorColumn;
import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.Id;
import proj.w41k4z.orm.annotation.relationship.Inheritance;
import proj.w41k4z.orm.enumeration.InheritanceType;

@Entity(table = "pending_movement")
@Inheritance(type = InheritanceType.SAME_TABLE)
@DiscriminatorColumn("action_type")
public abstract class PendingMovement extends StockMovement {

    @Id
    @Column
    private Long id;

    @Column(name = "validation_date")
    private Timestamp validationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long stockMovementId) {
        this.id = stockMovementId;
    }

    public Timestamp getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Timestamp validationDate) {
        this.validationDate = validationDate;
    }
}
