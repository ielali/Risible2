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

package risible.core.foo;

import risible.core.MediaType;
import risible.core.annotations.HeaderParam;
import risible.core.annotations.Produces;
import risible.core.annotations.QueryParam;
import risible.core.annotations.UsesInvoker;

import java.lang.reflect.Method;
import java.util.Date;

public class ControllerForTesting {
    public static final Method DOIT_METHOD;
    public static final Method PARAMS_METHOD;
    public static final Method ANNOTATED_METHOD;

    @QueryParam
    private Date doitDate;
    @HeaderParam("myHeader")
    private int doitSize;
    private boolean doitWasInvoked;

    static {
        DOIT_METHOD = getMethod("doit");
        ANNOTATED_METHOD = getMethod("anOtatedMethod");
        PARAMS_METHOD = getMethod("methodWithParams");
    }

    private ControllerForTesting.DummyBean dummyBean;

    private static Method getMethod(String name) {
        Method doit = null;
        try {
            doit = ControllerForTesting.class.getMethod(name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return doit;
    }

    public String methodWithParams() {
        doitWasInvoked = true;
        return "sucks_S";
    }

    public String doitAgain(int times) {
        return "ok";
    }

    public String doit() {
        doitWasInvoked = true;
        return "sucks_S";
    }

    @UsesInvoker("other")
    public String anOtatedMethod() {
        return "foo";
    }

    @Produces(MediaType.APPLICATION_JSON)
    public DummyBean processBean() {
        dummyBean = new DummyBean("John", "Smith");
        return dummyBean;
    }

    public DummyBean getDummyBean() {
        return dummyBean;
    }

    public Date getDoitDate() {
        return doitDate;
    }

    public void setDoitDate(Date doitDate) {
        this.doitDate = doitDate;
    }

    public boolean wasDoitInvoked() {
        return doitWasInvoked;
    }

    public int getDoitSize() {
        return doitSize;
    }

    public void setDoitSize(int doitSize) {
        this.doitSize = doitSize;
    }

    private class DummyBean {
        private String firstName;
        private String lastName;

        private DummyBean(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        private String getFirstName() {
            return firstName;
        }

        private void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        private String getLastName() {
            return lastName;
        }

        private void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
