package proj.w41k4z.orm.annotation.relationship;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation must be used with relationship annotation {@link OneToOne}
 * and {@link ManyToOne}
 * This contains the column used for the JOIN operation
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
    /**
     * The column name from the declaring entity used for the join
     * 
     * @return the column to JOIN on
     */
    String column();

    /**
     * Tells if the column can have NULL value
     * 
     * @return a boolean telling nullable or not
     */
    boolean nullable() default false;
}
