package proj.w41k4z.orm.annotation.relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import proj.w41k4z.orm.enumeration.InheritanceType;

/**
 * This annotation is used to specify that the target class is a parent entity.
 * 
 * @see {@link InheritanceType}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inheritance {
    /**
     * The inheritance type.
     * 
     * @return InheritanceType the inheritance type
     */
    InheritanceType type();
}
