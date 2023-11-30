package proj.w41k4z.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the name of the property in the
 * configuration file.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
    /**
     * This is the name of the property in the configuration file.
     * 
     * @return String the name of the property
     */
    String value();
}
