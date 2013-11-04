package risible.core.annotations;

import risible.core.MediaType;

import java.lang.annotation.*;


/**
 * Defines the media type that a given Renderer can handle.
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Renders {
    /**
     * A  media type. E.g. "text/html".
     */
    String value() default MediaType.TEXT_HTML;
}

