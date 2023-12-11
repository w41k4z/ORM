package proj.w41k4z.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the value of a discriminator column.
 * Inheritance of type SAME_TABLE child class must have this annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscriminatorValue {
    /**
     * This is the value of the discriminator column in the table. This value is
     * used when querying the table as a perimeter clause.
     * 
     * @return String the value of the discriminator column
     */
    String value();
}
