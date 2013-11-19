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

import junit.framework.TestCase;
import risible.core.annotations.HeaderParam;
import risible.core.annotations.QueryParam;
import risible.core.dispatch.DataBindingActionInvoker;
import risible.core.dispatch.Invocation;
import risible.core.dispatch.InvocationFailed;
import risible.core.foo.ControllerForTesting;

import java.lang.annotation.Annotation;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DefaultActionInvokerTest extends TestCase {

    public void testInvokerSetsParametersAndInvokesMethod() throws Exception {
        DataBindingActionInvoker invoker = new DataBindingActionInvoker();
        invoker.setTypeConverter(new TypeConverter());
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        TreeMap<String, Object> headerParams = new TreeMap<String, Object>();
        params.put("doitDate", new String[]{"12-Mar-2004"});
        headerParams.put("myHeader", new String[]{"12"});
        ControllerForTesting controller = new ControllerForTesting();
        Invocation i = new Invocation(ControllerForTesting.class, ControllerForTesting.DOIT_METHOD, new String[0], null, "");
        Map<Class<? extends Annotation>, TreeMap<String, Object>> parameters = new HashMap<Class<? extends Annotation>, TreeMap<String, Object>>();
        parameters.put(QueryParam.class, params);
        parameters.put(HeaderParam.class, headerParams);
        invoker.invoke(controller, i, parameters);

        assertTrue(controller.wasDoitInvoked());
        assertEquals(new GregorianCalendar(2004, 2, 12).getTime(), controller.getDoitDate());
        assertEquals(12, controller.getDoitSize());
    }

    public void testInvokerDoesntTryToInvokeNullMethod() throws IllegalAccessException, InvocationFailed {
        try {
            new Invocation(ControllerForTesting.class, null, new String[0], null, "");
            fail();
        } catch (Exception e) {
        }
    }
}
