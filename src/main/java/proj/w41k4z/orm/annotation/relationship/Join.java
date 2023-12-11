package proj.w41k4z.orm.annotation.relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation must be used with relationship annotation {@link OneToMany}
 * and {@link ManyToMany} This contains the column used for the JOIN operation
 * and the join table name.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Join {

    /**
     * The join table name. Meant to be used with {@link ManyToMany} annotation and
     * will be ignored when used with {@link OneToMany}.
     * 
     * @return the join table name
     */
    String table() default "";

    /**
     * The column name from the join table that will be joined with the declaring
     * entity column id. Meant to be used with {@link ManyToMany} annotation and
     * will be ignored when used with {@link OneToMany}.
     * 
     * @return the column name
     */
    String joinColumn() default "";

    /**
     * The column name from the join table that will be joined with the inverse
     * entity column id. Meant to be used with both {@link ManyToMany} annotation
     * and {@link OneToMany}.
     * 
     * @return the column name
     */
    String inverseJoinColumn() default "";
}
