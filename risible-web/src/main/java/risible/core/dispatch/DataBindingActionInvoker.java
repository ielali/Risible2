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

import ognl.*;
import org.apache.commons.lang.StringUtils;
import risible.core.annotations.HeaderParam;
import risible.core.annotations.PathParam;
import risible.core.annotations.QueryParam;
import risible.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class DataBindingActionInvoker implements Invoker {
    private risible.core.TypeConverter typeConverter;

    public Object invoke(Object controller, Invocation invocation, Map requestParameters, Map headerParameters) throws IllegalAccessException, InvocationFailed {
        dataBindControllerFields(controller, invocation, requestParameters, headerParameters);
        Object[] args = dataBindMethodParams(invocation, requestParameters, headerParameters);
        try {
            return invocation.getActionMethod().invoke(controller, args);
        } catch (InvocationTargetException e) {
            throw new InvocationFailed(e.getTargetException());
        }
    }

    protected void dataBindControllerFields(Object controller, Invocation invocation, Map requestParameters, Map headerParameters) {
        String[] pathParameters = invocation.getPathParameters();
        for (Field field : controller.getClass().getDeclaredFields()) {
            PathParam pathParam = field.getAnnotation(PathParam.class);
            if (pathParam != null) {
                int pathIndex = pathParam.value();
                if (pathIndex < pathParameters.length) {
                    populate(controller, field.getName(), pathParameters[pathIndex]);
                }
            }
            HeaderParam headerParam = field.getAnnotation(HeaderParam.class);
            if (headerParam != null) {
                String headerParamName = StringUtils.isNotBlank(headerParam.value()) ? headerParam.value() : field.getName();
                populate(controller, field.getName(), headerParameters.get(headerParamName));
            }
            QueryParam queryParam = field.getAnnotation(QueryParam.class);
            if (queryParam != null) {
                String requestParamName = StringUtils.isNotBlank(queryParam.value()) ? queryParam.value() : field.getName();
                if (requestParamName != null && requestParamName.trim() != "") {
                    String requestParamNameRoot = requestParamName + ".";
                    for (Object o : requestParameters.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        String key = ((String) entry.getKey()).trim();
                        if (key != null) {
                            if (key.equals(requestParamName) || key.startsWith(requestParamNameRoot)) {
                                populate(controller, key, entry.getValue());
                            }
                        }
                    }
                }
            }
        }
    }

    private Object[] dataBindMethodParams(Invocation invocation, Map requestParameters, Map headerParameters) {
        Class[] types = invocation.getActionMethod().getParameterTypes();
        Object[] result = new Object[types.length];
        if (types.length > 0) {
            Annotation[][] paramAnnotations = invocation.getActionMethod().getParameterAnnotations();
            String[] pathParameters = invocation.getPathParameters();
            for (int i = 0; i < types.length; i++) {
                PathParam pathParam = ClassUtils.getAnnotation(paramAnnotations[i], PathParam.class);
                if (pathParam != null) {
                    int pathIndex = pathParam.value();
                    if (pathIndex < pathParameters.length) {
                        result[i] = convert(pathParameters[pathIndex], types[i]);
                    }
                }
                HeaderParam headerParam = ClassUtils.getAnnotation(paramAnnotations[i], HeaderParam.class);
                if (headerParam != null) {
                    String headerParamName = headerParam.value();
                    if (StringUtils.isNotBlank(headerParamName)) {
                        result[i] = convert(headerParameters.get(headerParamName), types[i]);
                    }
                }
                QueryParam queryParam = ClassUtils.getAnnotation(paramAnnotations[i], QueryParam.class);
                if (queryParam != null) {
                    String requestParamName = queryParam.value();
                    if (requestParamName != null && requestParamName.trim() != "") {
                        String requestParamNameRoot = requestParamName + ".";
                        for (Object o : requestParameters.entrySet()) {
                            Map.Entry entry = (Map.Entry) o;
                            String key = ((String) entry.getKey()).trim();
                            if (key != null) {
                                if (requestParamName.equals(key)) {
                                    result[i] = convert(entry.getValue(), types[i]);
                                } else if (key.startsWith(requestParamNameRoot)) {
                                    populate(result[i], key.replace(requestParamNameRoot, ""), entry.getValue());
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private Object convert(Object parameter, Class type) {
        return typeConverter.convertValue(null, null, null, null, parameter, type);
    }

    private void populate(Object target, String propertyName, Object value) {
        OgnlContext context = new OgnlContext();
        context.setTypeConverter(typeConverter);
        context.setMemberAccess(new DefaultMemberAccess(true, true, true));
        try {
            Ognl.setValue(propertyName, context, target, value);
        } catch (NoSuchPropertyException e) { //
        } catch (OgnlException e) {
            Throwable cause = e.getReason();
            if (cause != null) {
                throw new RuntimeException("Setting " + propertyName + " to " + stringify(value), cause);
            }
            throw new RuntimeException("Setting '" + propertyName + "' to " + stringify(value), e);
        }
    }

    private String stringify(Object o) {
        if (o == null) {
            return "*null*";
        } else if (o.getClass().isArray()) {
            Object[] a = (Object[]) o;
            return new ArrayList(Arrays.asList(a)).toString();
        } else {
            return String.valueOf(o);
        }
    }

    public void setTypeConverter(risible.core.TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }
}
