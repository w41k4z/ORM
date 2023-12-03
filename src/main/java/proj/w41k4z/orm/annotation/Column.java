package proj.w41k4z.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify that the target field is a column.
 * It can also contain additional information related to the column it is mapped
 * to, such as the column name...
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * The column name. If none, the field name will the default column name
     * 
     * @return String the column name
     */
    String name() default "";
}
