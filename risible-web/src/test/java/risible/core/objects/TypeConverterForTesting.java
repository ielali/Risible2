// Copyright 2009 Conan Dalton and Jean-Philippe Hallot
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

package risible.core.objects;

import risible.core.TypeConverter;

import java.lang.reflect.Member;
import java.util.Map;

public class TypeConverterForTesting extends TypeConverter {
    public Object convertValue(Map map, Object o, Member member, String s, Object value, Class type) {
        if (ComplexType.class.isAssignableFrom(type)) {
            return new ComplexType(Integer.valueOf((String) value));
        } else return super.convertValue(map, o, member, s, value, type);
    }
}
