package risible.demo.app.util;

import risible.demo.app.domain.User;
import risible.util.ClassUtils;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 10/15/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassUtilsTest {
    @org.junit.Test
    public void testGetParameterizedType() throws Exception {
        assertEquals(User.class.getGenericSuperclass(), ClassUtils.getParameterizedType(User.class));
    }

    @org.junit.Test
    public void testGetParameterizedTypes() throws Exception {
        Type[] parameterizedTypes = ClassUtils.getParameterizedTypes(User.class);
        assertEquals(1, parameterizedTypes.length);
        assertEquals(Integer.class, parameterizedTypes[0]);

    }

    @org.junit.Test
    public void testGetGenericType() throws Exception {
        Type[] genericTypes = ClassUtils.getGenericTypes(User.class);
        assertEquals(1,genericTypes.length);
        assertEquals(User.class.getGenericSuperclass(),genericTypes[0]);
    }
}
