package proj.w41k4z.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify that the target class in an entity.
 * It can also contain additional information related to the table it is mapped
 * to, such as the table name...
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    /**
     * The table name. If none, the class name will the default table name
     * 
     * @return String the table name
     */
    String table() default "";
}