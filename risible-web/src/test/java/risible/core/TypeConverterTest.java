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


package risible.core;

import ognl.Ognl;
import ognl.OgnlContext;
import org.junit.Assert;
import org.junit.Test;
import risible.core.objects.ComplexControllerForTesting;
import risible.core.objects.TypeConverterForTesting;

public class TypeConverterTest extends TestableFilter {

    @Test
    public void testOgnlRetrievesComplexValues() throws Exception {
        ComplexControllerForTesting controller = new ComplexControllerForTesting();
        OgnlContext context = new OgnlContext();
        context.setTypeConverter(new TypeConverterForTesting());
        Ognl.setValue("complexValue", context, controller, "9970");
        Assert.assertEquals(9970, controller.getComplexValue().value);
    }

    @Test
    public void testOgnlRetrievesComplexValuesInArray() throws Exception {
        ComplexControllerForTesting controller = new ComplexControllerForTesting();
        OgnlContext context = new OgnlContext();
        context.setTypeConverter(new TypeConverterForTesting());
        Ognl.setValue("complexValueList[1]", context, controller, "9971");
        Assert.assertEquals(null, controller.getComplexValueList()[0]);
        Assert.assertEquals(9971, controller.getComplexValueList()[1].value);
        Assert.assertEquals(null, controller.getComplexValueList()[2]);
    }

    @Test
    public void testOgnlRetrievesAnArrayIndexedByStringWithoutTypeConverssion() throws Exception {
        ComplexControllerForTesting controller = new ComplexControllerForTesting();
        OgnlContext context = new OgnlContext();
        context.setTypeConverter(new TypeConverterForTesting());
        Ognl.setValue("stringValueMapByString[\"stringIndex\"]", context, controller, "stringValue");
        Assert.assertEquals("stringValue", controller.getStringValueMapByString().get("stringIndex"));
    }

    /**
     * These tests are known to fail. We have to hask OGNL to make them run as we want.
     *
     * Note: it appears that OGNL, when seeing a List or Map not indexed by integers, do getProperty.add(indice,value).
     * So it has no chance to know that there is conversion to make...
     */
  /*
      @Test public void testOgnlRetrievesAnArrayIndexedByComplexType() throws Exception {
          prepare(new Filter());
          ComplexControllerForTesting controller = new ComplexControllerForTesting();
          OgnlContext context = new OgnlContext();
          context.setTypeConverter(new TypeConverterForTesting());
          Ognl.setValue("complexValueMap[\"1\"]", context, controller, "9972");
          Assert.assertEquals(null, controller.getComplexValueMap().get(new ComplexType(0)));
          assertEquals(9972, controller.getComplexValueMap().get(new ComplexType(1)).value);
          assertEquals(null, controller.getComplexValueMap().get(new ComplexType(2)));
      }
      @Test public void testOgnlRetrievesAnArrayIndexedByString() throws Exception {
          prepare(new Filter());
          ComplexControllerForTesting controller = new ComplexControllerForTesting();
          OgnlContext context = new OgnlContext();
          context.setTypeConverter(new TypeConverterForTesting());
          Ognl.setValue("complexValueMapByString[\"jojo\"]", context, controller, "9973");
          assertEquals(9973, controller.getComplexValueMapByString().get("jojo"));
      }
  */
}
