package proj.w41k4z.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import proj.w41k4z.orm.enumeration.GenerationType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Generated {
    GenerationType type() default GenerationType.AUTO;

    String sequenceName() default "";

    String pkPrefix() default "";

    int pkLength() default 0;
}
