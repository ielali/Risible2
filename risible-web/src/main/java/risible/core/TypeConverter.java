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

package risible.core;

import ognl.DefaultTypeConverter;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TypeConverter extends DefaultTypeConverter {
    private static Map<Class, Method> javaNumberParsers = new HashMap<Class, Method>();

    static {
        try {
            javaNumberParsers.put(Integer.class, Integer.class.getMethod("valueOf", String.class));
            javaNumberParsers.put(Integer.TYPE, Integer.class.getMethod("valueOf", String.class));
            javaNumberParsers.put(Long.class, Long.class.getMethod("valueOf", String.class));
            javaNumberParsers.put(Long.TYPE, Long.class.getMethod("valueOf", String.class));
            javaNumberParsers.put(Double.class, Double.class.getMethod("valueOf", String.class));
            javaNumberParsers.put(Double.TYPE, Double.class.getMethod("valueOf", String.class));
            javaNumberParsers.put(Float.class, Float.class.getMethod("valueOf", String.class));
            javaNumberParsers.put(Float.TYPE, Float.class.getMethod("valueOf", String.class));
        } catch (NoSuchMethodException e) {
            throw new Error("this should never, ever happen", e);
        }
    }

    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        String stringValue = toSingleString(value);

        Object primitive = convertPrimitives(propertyName, toType, stringValue);
        if (primitive != null) {
            return primitive;
        }
        return super.convertValue(context, target, member, propertyName, value, toType);


    }

    protected Object convertPrimitives(String propertyName, Class toType, String stringValue) {
        if (javaNumberParsers.containsKey(toType)) {
            return convertToNumber(propertyName, toType, stringValue);
        } else if (toType == Boolean.class || toType == Boolean.TYPE) {
            return "yes".equalsIgnoreCase(stringValue);
        } else if (toType == Date.class) {
            return convertToDate(stringValue, propertyName);
        } else if (toType == String.class) {
            return stringValue;
        }
        return null;
    }

    private Object convertToNumber(String propertyName, Class toType, String stringValue) {
        try {
            return stringValue == null ? null : javaNumberParsers.get(toType).invoke(null, stringValue);
        } catch (Exception e) {
            throw new TypeConversionFailed(propertyName, stringValue, toType, e);
        }
    }

    private Object convertToDate(String stringValue, String propertyName) {
        if (stringValue == null) {
            return null;
        }
        try {
            return new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH).parse(stringValue);
        } catch (ParseException e) {
            try {
                return new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).parse(stringValue);
            } catch (ParseException e1) {
                throw new TypeConversionFailed(propertyName, stringValue, Date.class, e1);
            }
        }
    }

    protected String toSingleString(Object from) {
        String result = "";
        if ((from != null) && (from.getClass().isArray())) {
            Object[] strings = (Object[]) from;
            if (strings.length > 0) {
                result = String.valueOf(strings[0]);
            }
        } else {
            result = String.valueOf(from);
        }
        return "".equals(result) ? null : result;
    }

    protected void dumpParameters(Object from, Class toClass, Member member, String property, Map context) {
        System.out.println("TypeConverter.convertValue");
        if (from.getClass().isArray()) {
            System.out.println("from array " + new ArrayList(Arrays.asList((Object[]) from)));
        } else {
            System.out.println("from item " + String.valueOf(from));
        }
        System.out.println("member = " + member);
        System.out.println("property = " + property);
        System.out.println("toClass = " + toClass);
        System.out.println("context=\n" + toString(context));
    }

    public static String toString(Map map) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[\n");
        try {
            for (Object o : map.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                Object v = entry.getValue();
                buffer.append(" [").append(String.valueOf(entry.getKey())).append(" : ").append(toString(v)).append("]\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        buffer.append("\n]");
        return buffer.toString();
    }

    private static String toString(Object v) {
        if (v.getClass().isArray()) {
            v = new ArrayList(Arrays.asList((Object[]) v));
        }
        return String.valueOf(v);
    }
}
