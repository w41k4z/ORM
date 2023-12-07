package proj.w41k4z.orm.annotation.relationship;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation must be used with relationship annotation such as
 * {@link OneToOne}, {@link OneToMany}, {@link ManyToMany}...
 * This contains the column used for the JOIN operation
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
    /**
     * The column name
     * 
     * @return the column to JOIN on
     */
    String column();
}