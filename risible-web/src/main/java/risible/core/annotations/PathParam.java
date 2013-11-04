package risible.core.annotations;


/*
 * PathParam.java
 *
 * Created on November 16, 2006, 2:04 PM
 *
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathParam {
    /*
     * The index of the path entry after the controller and method entries
     * com/company/Controller/action/firstParam/secondParam
     * the index of "firstParam" would be 0, that of "secondPram" would be 1.
     */
    int value() default 0;
}
