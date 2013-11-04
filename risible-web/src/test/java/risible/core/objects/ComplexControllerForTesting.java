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

import java.util.HashMap;
import java.util.Map;

public class ComplexControllerForTesting {
    private ComplexType complexValue;
    private ComplexType[] complexValueList = new ComplexType[]{null, null, null, null};
    private Map<ComplexType, ComplexType> complexValueMap = new HashMap<ComplexType, ComplexType>();
    private Map<String, ComplexType> complexValueMapByString = new HashMap<String, ComplexType>();
    private Map<String, String> stringValueMapByString = new HashMap<String, String>();

    public void setComplexValue(ComplexType complexValue) {
        this.complexValue = complexValue;
    }

    public void setComplexValueList(int indice, ComplexType value) {
        complexValueList[indice] = value;
    }

    public void setComplexValueMap(ComplexType indice, ComplexType value) {
        complexValueMap.put(indice, value);
        throw new NeverAccessed("Never accessed by OGNL, for list and maps, OGNL seems to switch on getProperty");
    }

    public void setComplexValueMapByString(String indice, ComplexType value) {
        complexValueMapByString.put(indice, value);
        throw new NeverAccessed("Never accessed by OGNL, for list and maps, OGNL seems to switch on getProperty");
    }

    public ComplexType getComplexValue() {
        return complexValue;
    }

    public ComplexType[] getComplexValueList() {
        return complexValueList;
    }

    public Map<ComplexType, ComplexType> getComplexValueMap() {
        return complexValueMap;
    }

    public Map<String, ComplexType> getComplexValueMapByString() {
        return complexValueMapByString;
    }

    public Map<String, String> getStringValueMapByString() {
        return stringValueMapByString;
    }

    private class NeverAccessed extends RuntimeException {
        public NeverAccessed(String message) {
            super(message);
        }
    }
}
