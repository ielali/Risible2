package risible.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtils {
    static public ParameterizedType getParameterizedType(Class<?> target) {
        Type[] types = getGenericTypes(target);
        if (types.length > 0 && types[0] instanceof ParameterizedType) {
            return (ParameterizedType) types[0];
        }
        return null;
    }

    static public Type[] getParameterizedTypes(Class<?> target) {
        Type[] types = getGenericTypes(target);
        if (types.length > 0 && types[0] instanceof ParameterizedType) {
            return ((ParameterizedType) types[0]).getActualTypeArguments();
        }
        return null;
    }

    static public Type[] getGenericTypes(Class<?> target) {
        if (target != null) {
            Type[] types = target.getGenericInterfaces();
            if (types.length > 0) {
                return types;
            }
            Type type = target.getGenericSuperclass();
            if (type != null) {
                if (type instanceof ParameterizedType) {
                    return new Type[]{type};
                }
            }
        }
        return new Type[0];
    }

    public static <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> annotationType) {
        if (annotations != null && annotationType != null) {
            for (Annotation anAnnotation : annotations) {
                if (anAnnotation.annotationType().equals(annotationType)) {
                    return (T) anAnnotation;
                }
            }
        }
        return null;
    }
}

