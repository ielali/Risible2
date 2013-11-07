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

package risible.freemarker;

import org.junit.Assert;
import org.junit.Test;
import risible.core.render.ExceptionTemplateNameGenerator;
import risible.core.render.MissingTemplate;
import risible.core.foo.ControllerExceptionSubclassForTesting;
import risible.core.foo.ControllerForTesting;
import risible.core.render.TemplateNameGenerator;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class ExceptionTemplateNameGeneratorTest {

    @Test
    public void testConsidersAllCombinationsOfExceptionSuperclassesAndControllerClassAndActionName() {
        Iterator i = new ExceptionTemplateNameGenerator(
                new ControllerExceptionSubclassForTesting(),
                ControllerForTesting.class,
                "doit");

        assertEquals("risible/core/foo/ControllerForTesting-doit-ControllerExceptionSubclassForTesting.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/doit-ControllerExceptionSubclassForTesting.ftl", i.next());
        assertEquals("risible/core/foo/ControllerForTesting-ControllerExceptionSubclassForTesting.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/ControllerExceptionSubclassForTesting.ftl", i.next());
        assertEquals("risible/core/foo/ControllerExceptionSubclassForTesting.ftl", i.next());
        assertEquals("ControllerExceptionSubclassForTesting.ftl", i.next());

        assertEquals("risible/core/foo/ControllerForTesting-doit-ControllerExceptionForTesting.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/doit-ControllerExceptionForTesting.ftl", i.next());
        assertEquals("risible/core/foo/ControllerForTesting-ControllerExceptionForTesting.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/ControllerExceptionForTesting.ftl", i.next());
        assertEquals("risible/core/foo/ControllerExceptionForTesting.ftl", i.next());
        assertEquals("ControllerExceptionForTesting.ftl", i.next());

        assertEquals("risible/core/foo/ControllerForTesting-doit-Exception.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/doit-Exception.ftl", i.next());
        assertEquals("risible/core/foo/ControllerForTesting-Exception.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/Exception.ftl", i.next());
        assertEquals("risible/core/foo/Exception.ftl", i.next());
        assertEquals("Exception.ftl", i.next());

        assertEquals("risible/core/foo/ControllerForTesting-doit-Throwable.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/doit-Throwable.ftl", i.next());
        assertEquals("risible/core/foo/ControllerForTesting-Throwable.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/Throwable.ftl", i.next());
        assertEquals("risible/core/foo/Throwable.ftl", i.next());
        assertEquals("Throwable.ftl", i.next());

        assertEquals("risible/core/foo/ControllerForTesting-doit-Object.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/doit-Object.ftl", i.next());
        assertEquals("risible/core/foo/ControllerForTesting-Object.ftl", i.next());
        assertEquals("risible/core/foo/controllerForTesting/Object.ftl", i.next());
        assertEquals("risible/core/foo/Object.ftl", i.next());
        assertEquals("Object.ftl", i.next());
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void testReportsOriginalExceptionClassWhenNoTemplateIsFound() {
        TemplateNameGenerator g = new ExceptionTemplateNameGenerator(
                new IllegalArgumentException(),
                ControllerForTesting.class,
                "doit");

        try {
            g.getTemplateName();
        } catch (MissingTemplate mt) {
            Assert.assertTrue(mt.getMessage().contains(IllegalArgumentException.class.getName()));
        }
    }
}
