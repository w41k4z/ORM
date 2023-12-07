package proj.w41k4z.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the name of the discriminator column.
 * Inheritance of type SAME_TABLE parent class must have this annotation.
 * Note that you do not need to have a field for this unique column
 * 
 * @see {@link DiscriminatorValue}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscriminatorColumn {
    /**
     * This is the name of the discriminator column in the table
     * 
     * @return String the name of the column
     */
    String value();
}
