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
import risible.core.dispatch.Invocation;
import risible.core.dispatch.InvocationFailed;
import risible.core.foo.ControllerForTesting;

public class InvocationTest extends TestCase {

    public void testInvocationDeterminesExtension() throws InvocationFailed {
        Invocation i = Invocation.create("risible.core.foo", "controllerForTesting/doitAgain/99.png");
        assertEquals("png", i.getExtension());
        assertEquals("/controllerForTesting/doitAgain", i.getInvocationPath());
        assertEquals(1, i.getPathParameters().length);
        assertEquals("99", i.getPathParameters()[0]);
        assertEquals(ControllerForTesting.class, i.getTargetClass());
    }
}
