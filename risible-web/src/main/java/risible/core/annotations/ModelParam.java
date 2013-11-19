package risible.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 19/11/13
 * Time: 01:38
 * When annotating a method parameter of type java.util.Map, it will be instantiated and passed to the controller method.
 * The contents of the Map will be then available to the renderer. The main purpose of this annotation is to allow to communicate
 * objects to the renderer without exposing explicit getter fields at the controller level and thus avoid having state.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelParam {
    String value();
}
