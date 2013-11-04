package risible.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Argv {

    public static void setArgs(String[] argv, Object collector, String defaultArg, String terminalArgRegex) throws IllegalAccessException, InvocationTargetException {
        String argName = defaultArg;
        List argValue = new ArrayList();
        for (String s : argv) {
            if (s.startsWith("-") && (terminalArgRegex == null || !argName.matches(terminalArgRegex))) {
                if (argName != null) {
                    setArg(argName, argValue, collector);
                }
                argName = s;
                argValue.clear();
            } else {
                argValue.add(s);
            }
        }
        setArg(argName, argValue, collector);
    }

    private static void setArg(String argName, List argValue, Object collector) throws IllegalAccessException, InvocationTargetException {
        argName = argName.replaceFirst("-{1,2}", "");
        for (Method method : collector.getClass().getMethods()) {
            if (matches(method, argName)) {
                setArgValue(collector, argName, method, argValue);
            }
        }
    }

    private static void setArgValue(Object collector, String argName, Method method, List argValue) throws IllegalAccessException, InvocationTargetException {
        Class[] params = method.getParameterTypes();
        if (params.length > 1) {
            throw new Error("too many parameters on method " + method);
        }

        Class param = params[0];
        if (param == Boolean.class || param == Boolean.TYPE) {
            method.invoke(collector, Boolean.TRUE);
        } else if (param == Integer.class || param == Integer.TYPE) {
            method.invoke(collector, asInt(argName, argValue));
        } else if (param == String.class) {
            method.invoke(collector, asString(argName, argValue));
        } else if (param == List.class || param == Collection.class) {
            method.invoke(collector, argValue);
        }
    }

    private static Integer asInt(String argName, List argValue) {
        checkUnique(argName, argValue);
        return Integer.parseInt((String) argValue.get(0));
    }

    private static String asString(String argName, List argValue) {
        checkUnique(argName, argValue);
        return (String) argValue.get(0);
    }

    private static void checkUnique(String argName, List argValue) {
        if (argValue.size() > 1) {
            throw new Error("Argument " + argName + " expects one value, got " + argValue);
        }
    }

    private static boolean matches(Method method, String argName) {
        if (!method.getName().startsWith("set")) {
            return false;
        }

        if (method.getName().equalsIgnoreCase("set" + argName)) {
            return true;
        } else {
            ShortName s = method.getAnnotation(ShortName.class);
            if (s != null) {
                return s.value().equalsIgnoreCase(argName);
            }
        }

        return false;
    }
}
