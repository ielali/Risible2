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

import org.junit.Before;
import org.junit.Test;
import risible.core.foo.ControllerExceptionSubclassForTesting;
import risible.core.foo.ControllerForTesting;
import risible.core.render.RendererContext;
import risible.json.JsonRenderer;

import java.io.StringWriter;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;


public class JsonRendererTest {
    private JsonRenderer renderer;

    @Before
    public void setUp() throws Exception {
        renderer = new JsonRenderer();
        renderer.afterPropertiesSet();
    }

    @Test
    public void testRendererIncludesParsedFiles() throws Exception {
        StringWriter writer = new StringWriter();
        ControllerForTesting controller = new ControllerForTesting();
        RendererContext context = new RendererContext(controller, "processBean", controller.processBean(), null, new HashMap<String, Object>(), writer);
        renderer.render(context);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Smith\"}", writer.toString());
    }

    @Test
    public void testRendererFindsTemplateForExceptionSuperclass() throws Exception {
        StringWriter writer = new StringWriter();
        ControllerForTesting controller = new ControllerForTesting();
        RendererContext context = new RendererContext(controller, "processBean", null, new ControllerExceptionSubclassForTesting("Message", new RuntimeException("test")), new HashMap<String, Object>(), writer);
        renderer.render(context);
        assertEquals("{\"detailMessage\":\"Message\",\"cause\":{\"cause\":\"null\",\"message\":\"test\"},\"stackTrace\":[],\"suppressedExceptions\":[]}", writer.toString());
    }
}
