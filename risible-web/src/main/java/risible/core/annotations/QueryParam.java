package risible.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds the value(s) of a HTTP query parameter to a resource method parameter,
 * resource class field, or resource class bean property.
 *
 *
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParam {
    /**
     * Defines the name of the HTTP query parameter whose value will be used
     * to initialize the value of the annotated method argument, class field or
     * bean property. The name is specified in decoded form, any percent encoded
     * literals within the value will not be decoded and will instead be
     * treated as literal text. E.g. if the parameter name is "a b" then the
     * value of the annotation is "a b", <i>not</i> "a+b" or "a%20b".
     */
    String value() default "";
}
