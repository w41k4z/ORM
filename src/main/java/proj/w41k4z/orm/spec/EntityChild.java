package proj.w41k4z.orm.spec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.relationship.Join;
import proj.w41k4z.orm.annotation.relationship.ManyToMany;
import proj.w41k4z.orm.annotation.relationship.ManyToOne;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.annotation.relationship.OneToOne;

/**
 * This class is a representation of an entity child.
 */
public class EntityChild {

    private Field field;
    private Integer rank;

    /**
     * Default constructor. This is meant to be used by the EntityAccess (which
     * handle some checks) so it can not be instatiated from else where.
     * 
     * @param field the entity field
     */
    protected EntityChild(Field field, Integer rank) {
        if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Join.class)) {
            this.field = field;
            this.rank = rank;
        } else {
            throw new IllegalArgumentException(
                    "The annotation @Column or @Join is missing for this child relationship. Do not forget to use one of these annotation based on your field relationship. Source: `"
                            + field.getDeclaringClass().getSimpleName() + "." + field.getName() + "`");
        }
    }

    /**
     * This method returns the entity class related to this entity child
     * 
     * @return the child entity class
     */
    public Class<?> getEntityClass() {
        return this.field.getType().isArray() ? this.field.getType().getComponentType() : this.field.getType();
    }

    /**
     * This method returns the child as a Field
     * 
     * @return the field
     */
    public Field getField() {
        return field;
    }

    /**
     * This method returns the child rank
     * 
     * @return the rank
     */
    public Integer getRank() {
        return rank;
    }

    public Class<? extends Annotation> getRelationshipAnnotation() {
        if (this.field.isAnnotationPresent(OneToOne.class)) {
            if (field.getType().isArray()) {
                throw new UnsupportedOperationException("One to One relationship field can not be an array: Source: `"
                        + this.field.getDeclaringClass().getSimpleName() + "." + this.field.getName() + "'");
            }
            return OneToOne.class;
        } else if (this.field.isAnnotationPresent(ManyToOne.class)) {
            if (field.getType().isArray()) {
                throw new UnsupportedOperationException("Many to One relationship field can not be an array: Source: `"
                        + this.field.getDeclaringClass().getSimpleName() + "." + this.field.getName() + "'");
            }
            return ManyToOne.class;
        } else if (this.field.isAnnotationPresent(OneToMany.class)) {
            if (!field.getType().isArray()) {
                throw new UnsupportedOperationException("One to Many relationship field has to be an array: Source: `"
                        + this.field.getDeclaringClass().getSimpleName() + "." + this.field.getName() + "'");
            }
            return OneToMany.class;
        } else if (this.field.isAnnotationPresent(ManyToMany.class)) {
            if (!field.getType().isArray()) {
                throw new UnsupportedOperationException("Many to Many relationship field has to be an array: Source: `"
                        + this.field.getDeclaringClass().getSimpleName() + "." + this.field.getName() + "'");
            }
            return ManyToMany.class;
        } else {
            throw new IllegalArgumentException(
                    "The annotation @OneToOne, @ManyToOne, @OneToMany or @ManyToMany is missing for this child relationship. Do not forget it. Source: `"
                            + field.getDeclaringClass().getSimpleName() + "." + field.getName() + "`");
        }
    }
}
