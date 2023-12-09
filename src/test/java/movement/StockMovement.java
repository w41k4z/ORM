package movement;

import java.sql.Timestamp;

import proj.w41k4z.orm.annotation.Entity;
import proj.w41k4z.orm.annotation.Id;
import proj.w41k4z.orm.annotation.relationship.Inheritance;
import proj.w41k4z.orm.annotation.relationship.Key;
import proj.w41k4z.orm.annotation.relationship.OneToOne;
import proj.w41k4z.orm.enumeration.InheritanceType;
import proj.w41k4z.orm.annotation.Column;

@Entity
@Inheritance(type = InheritanceType.DIFFERENT_TABLE)
public abstract class StockMovement {

    @Id
    @Column
    private Long id;

    @OneToOne
    @Key(column = "article_code", nullable = true)
    private Article article;

    private Double quantity;

    @Column(name = "action_date", nullable = true)
    private Timestamp actionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long stockMovementId) {
        this.id = stockMovementId;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Timestamp getActionDate() {
        return actionDate;
    }

    public void setActionDate(Timestamp actionDate) {
        this.actionDate = actionDate;
    }
}
