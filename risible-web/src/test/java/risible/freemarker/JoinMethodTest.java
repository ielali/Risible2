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

import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import junit.framework.TestCase;
import risible.util.Lists;

public class JoinMethodTest extends TestCase {
    public void testJoinsAllOfItsArgumentsIntoASingleStringSeparatedByTheFirstArgument() throws TemplateModelException {
        JoinMethod join = new JoinMethod();
        assertEquals("foo, bar", join.exec(Lists.build(new SimpleScalar(", "), new SimpleScalar("foo"), new SimpleScalar("bar"))));
    }

    public void testJoinsAllOfItsSecondArgumentIntoASingleStringSeparatedByTheFirstArgument() throws TemplateModelException {
        JoinMethod join = new JoinMethod();
        SimpleSequence ss = new SimpleSequence(Lists.build("foo", "bar"));
        assertEquals("foo, bar", join.exec(Lists.build(", ", ss)));
    }
}
