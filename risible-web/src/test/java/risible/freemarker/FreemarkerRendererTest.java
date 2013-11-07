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

import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;
import risible.core.foo.ControllerExceptionSubclassForTesting;
import risible.core.foo.ControllerForTesting;
import risible.core.render.RendererContext;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;


public class FreemarkerRendererTest {
    private FreemarkerRenderer renderer;

    @Before
    public void setUp() throws NoSuchMethodException, IOException, TemplateException {
        renderer = FreemarkerRendererMother.create();
    }

    @Test
    public void testRendererIncludesParsedFiles() throws Exception {
        StringWriter writer = new StringWriter();
        RendererContext context = new RendererContext(new ControllerForTesting(), "doit", "parse", null, new HashMap<String, Object>(), writer);
        renderer.render(context);
        assertEquals("this is some included content", writer.toString());
    }

    @Test
    public void testRendererFindsTemplateForExceptionSuperclass() throws Exception {
        StringWriter writer = new StringWriter();
        RendererContext context = new RendererContext(new ControllerForTesting(), "doit", null, new ControllerExceptionSubclassForTesting(), new HashMap<String, Object>(), writer);
        renderer.render(context);
        assertEquals("this is the ControllerExceptionForTesting template", writer.toString());
    }
}
