package proj.w41k4z.orm.spec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import proj.w41k4z.orm.annotation.relationship.Key;
import proj.w41k4z.orm.annotation.relationship.ManyToMany;
import proj.w41k4z.orm.annotation.relationship.ManyToOne;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.annotation.relationship.OneToOne;

/**
 * This class is a representation of an entity child.
 */
public class EntityChild {

    private Field field;

    /**
     * Default constructor. This is meant to be used by the EntityAccess (which
     * handle some checks) so it can not be instatiated from else where.
     * 
     * @param field the entity field
     */
    protected EntityChild(Field field) {
        if (!field.isAnnotationPresent(Key.class)) {
            throw new IllegalArgumentException(
                    "The annotation @Key is missing for this child relationship. Do not forget it. Source: `"
                            + field.getDeclaringClass().getSimpleName() + "." + field.getName() + "`");
        }
        this.field = field;
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

    public Class<? extends Annotation> getRelationshipAnnotation() {
        if (this.field.isAnnotationPresent(OneToOne.class)) {
            return OneToOne.class;
        } else if (this.field.isAnnotationPresent(ManyToOne.class)) {
            return ManyToOne.class;
        } else if (this.field.isAnnotationPresent(OneToMany.class)) {
            return OneToMany.class;
        } else if (this.field.isAnnotationPresent(ManyToMany.class)) {
            return ManyToMany.class;
        } else {
            throw new IllegalArgumentException(
                    "The annotation @OneToOne, @ManyToOne, @OneToMany or @ManyToMany is missing for this child relationship. Do not forget it. Source: `"
                            + field.getDeclaringClass().getSimpleName() + "." + field.getName() + "`");
        }
    }
}
