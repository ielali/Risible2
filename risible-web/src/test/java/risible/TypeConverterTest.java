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

package risible;

import org.junit.Test;
import risible.core.TypeConverter;

import java.text.DecimalFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TypeConverterTest {
    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void testConverterConvertsSimpleStrings() {
        TypeConverter tc = new TypeConverter();
        String[] strings = {"foo"};
        Object actual = tc.convertValue(null, null, null, null, strings, String.class);
        assertEquals("foo", actual);
    }

    @Test
    public void testConverterConvertsFloats() {
        TypeConverter tc = new TypeConverter();
        String[] strings = {"99.86"};
        Float actual = (Float) tc.convertValue(null, null, null, null, strings, Float.class);
        assertEquals("099.86", new DecimalFormat("000.00").format(actual));
    }

    @Test
    public void testConverterConvertsLongs() {
        TypeConverter tc = new TypeConverter();
        String[] strings = {"123456"};
        Long actual = (Long) tc.convertValue(null, null, null, null, strings, Long.class);
        assertEquals(new Long(123456), actual);
    }
}
