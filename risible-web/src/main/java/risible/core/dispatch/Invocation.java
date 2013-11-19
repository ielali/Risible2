// Copyright 2008 Conan Dalton and Jean-Philippe Hallot
//
// This file is part of risible-web.
//
// risible-web is free software: you can redistribute it and/or modify
// it under the terms of version 3 of the GNU Lesser General Public License as published by
// the Free Software Foundation
//
// risible-db is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// Copies of the GNU General Public License and the GNU Lesser General Public License
// are distributed with this software, see /GPL.txt and /LGPL.txt at the
// root of this distribution.
//

package risible.core.dispatch;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import risible.core.MediaType;
import risible.core.annotations.Produces;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Invocation {
    public static final String PATH_SEPARATOR = "/";
    private static TreeMap<String, InvocationCache> cache = new TreeMap<String, InvocationCache>();
    private Class targetClass;
    private String methodName;
    private Method actionMethod;
    private String[] pathParameters;
    private String invocationPath;
    private String extension;

    public Invocation(Class targetClass, Method actionMethod, String[] pathParameters, String invocationPath, String extension) {
        this.invocationPath = invocationPath;
        this.extension = extension;
        if (actionMethod == null) {
            throw new NullPointerException("Cannot invoke action " + targetClass.getName() + "." + methodName + " with null method");
        }
        this.targetClass = targetClass;
        this.actionMethod = actionMethod;
        this.methodName = actionMethod.getName();
        this.pathParameters = pathParameters;
    }

    public static Invocation create(String controllerPackage, String path) throws InvocationFailed {
        String[] pathParts = path.split(PATH_SEPARATOR);
        String extension = removeExtension(pathParts);
        Class targetClass = null;
        Method actionMethod;
        String methodName;
        String[] pathParameters;
        String invocationPath = null;
        InvocationCache invocationCache = null;
        for (String invocationPathKey : cache.keySet()) {
            if ((PATH_SEPARATOR + path).startsWith(invocationPathKey)) {
                invocationPath = invocationPathKey;
                invocationCache = cache.get(invocationPathKey);
                break;
            }
        }
        if (invocationCache == null) {
            int i = 0;
            for (; i < pathParts.length; i++) {
                try {
                    String c = controllerPackage;
                    if (i > 0) {
                        String[] subPath = (String[]) ArrayUtils.subarray(pathParts, 0, i);
                        subPath[i - 1] = StringUtils.capitalize(subPath[i - 1]);
                        for (String aSubPath : subPath) {
                            c += "." + aSubPath;
                        }
                    }
                    targetClass = Class.forName(c);
                    break;
                } catch (ClassNotFoundException e) { // try again
                }
            }
            if (targetClass == null) {
                throw new NotFound("No class found for invocation " + Arrays.asList(pathParts));
            }
            methodName = pathParts[i];
            actionMethod = findMethod(targetClass, methodName);
            invocationPath = buildInvocationPath(pathParts, i + 1);
            invocationCache = new InvocationCache(targetClass, actionMethod);
            cache.put(invocationPath, invocationCache);
        }
        int i = invocationPath.split(PATH_SEPARATOR).length - 1;
        List pathParameterList = new ArrayList(pathParts.length - i);
        for (; i < pathParts.length; i++) {
            pathParameterList.add(pathParts[i]);
        }
        pathParameters = (String[]) pathParameterList.toArray(new String[pathParameterList.size()]);
        return new Invocation(invocationCache.targetClass, invocationCache.actionMethod, pathParameters, invocationPath, extension);
    }

    private static String removeExtension(String[] path) {
        String last = path[path.length - 1];
        if (last.indexOf(".") > -1) {
            String[] methodDotExtension = last.split("\\.");
            path[path.length - 1] = methodDotExtension[0];
            return methodDotExtension[1];
        }
        return "";
    }

    private static String buildInvocationPath(String[] path, int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++) {
            sb.append("/");
            sb.append(path[j]);
        }
        return sb.toString();
    }

    private static Method findMethod(Class targetClass, String methodName) throws InvocationFailed {
        for (Method method : targetClass.getMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        throw new InvocationFailed("No method " + methodName + " found on class " + targetClass.getName());
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getPathParameters() {
        return pathParameters;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

    public String toString() {
        return "Invocation " + targetClass.getName() + "." + methodName + "(" + Arrays.asList(pathParameters) + ")";
    }

    public String getInvocationPath() {
        return invocationPath;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isVoid() {
        return Void.TYPE.equals(actionMethod.getReturnType());
    }

    public MediaType getMediaType() {
        Produces annotation = actionMethod.getAnnotation(Produces.class);
        if (annotation != null && annotation.value() != null) {
            MediaType mediaType = MediaType.valueOf(annotation.value());
            if (mediaType != null) {
                return mediaType;
            }
        }
        return MediaType.TEXT_HTML_TYPE;
    }

    private static class InvocationCache {
        Class targetClass = null;
        Method actionMethod;

        private InvocationCache(Class targetClass, Method actionMethod) {
            this.targetClass = targetClass;
            this.actionMethod = actionMethod;
        }
    }
}
