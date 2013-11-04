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

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import junit.framework.TestCase;
import risible.core.foo.ControllerForTesting;
import risible.core.render.ResultTemplateNameGenerator;
import risible.util.Lists;

import java.util.Iterator;

public class CamelMethodTest extends TestCase {

    public void testCamelsSingleArgument() throws TemplateModelException {
        CamelMethod camel = new CamelMethod();
        assertEquals("fooBar", camel.exec(Lists.build("Foo Bar")));
    }

    public void testCamelsListOfStrings() throws TemplateModelException {
        CamelMethod camel = new CamelMethod();
        SimpleSequence ss = new SimpleSequence(Lists.build("Foo Bar", "Toto Titi"));
        assertEquals("[fooBar, totoTiti]", camel.exec(Lists.build(ss)).toString());
    }

    public static class ResultTemplateNameGeneratorTest extends TestCase {
        public void testConsidersAllCombinationsOfControllerClassAndActionNameAndResult() {
            Iterator i = new ResultTemplateNameGenerator(ControllerForTesting.class, "doit", "yadda");
            checkSequence(i,
                    "risible/core/foo/ControllerForTesting-doit-yadda.ftl",
                    "risible/core/foo/controllerForTesting/doit-yadda.ftl",
                    "risible/core/foo/ControllerForTesting-yadda.ftl",
                    "risible/core/foo/controllerForTesting/yadda.ftl",
                    "risible/core/foo/doit-yadda.ftl",
                    "risible/core/foo/yadda.ftl",
                    "risible/core/foo/doit.ftl",
                    "risible/core/foo/ControllerForTesting-doit.ftl",
                    "risible/core/foo/controllerForTesting/doit.ftl",
                    "risible/core/foo/ControllerForTesting.ftl");
        }

        public void testFindsTemplateForControllerClassForNonExistentMethodAndNullResult() {
            Iterator i = new ResultTemplateNameGenerator(ControllerForTesting.class, "doesnotexist", null);
            checkSequence(i,
                    "risible/core/foo/doesnotexist.ftl",
                    "risible/core/foo/ControllerForTesting-doesnotexist.ftl",
                    "risible/core/foo/controllerForTesting/doesnotexist.ftl",
                    "risible/core/foo/ControllerForTesting.ftl");
        }

        public void testOmitsResultIfNull() {
            Iterator i = new ResultTemplateNameGenerator(ControllerForTesting.class, "doit", null);
            checkSequence(i,
                    "risible/core/foo/doit.ftl",
                    "risible/core/foo/ControllerForTesting-doit.ftl",
                    "risible/core/foo/controllerForTesting/doit.ftl",
                    "risible/core/foo/ControllerForTesting.ftl");
        }

        private void checkSequence(Iterator i, String... templateNames) {
            for (String templateName : templateNames) {
                assertTrue(i.hasNext());
                assertEquals(templateName, i.next());
            }
            assertFalse(i.hasNext());
        }
    }
}
