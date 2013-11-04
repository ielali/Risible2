package risible.core.annotations;


import risible.core.MediaType;

import java.lang.annotation.*;


/**
 * Defines the media type that the methods of a controller can produce.
 * If not specified then a container will assume text/html by default.
 *
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
    /**
     * A media type.  E.g. "image/jpeg"
     */
    String value() default MediaType.TEXT_HTML;
}

