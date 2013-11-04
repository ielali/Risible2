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
import org.mockito.Mockito;
import risible.core.foo.ControllerExceptionSubclassForTesting;
import risible.core.foo.ControllerForTesting;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class FreemarkerRendererTest {
    private HttpServletResponse res;
    private HttpServletRequest req;
    private ByteArrayOutputStream responseByteStream;
    private FreemarkerRenderer renderer;

    @Before
    public void setUp() throws NoSuchMethodException, IOException, TemplateException {
        req = Mockito.mock(HttpServletRequest.class);
        res = Mockito.mock(HttpServletResponse.class);
        when(req.getParameterMap()).thenReturn(new HashMap());
        when(req.getRequestURL()).thenReturn(new StringBuffer());
        when(req.getRequestURI()).thenReturn("/");
        responseByteStream = new ByteArrayOutputStream();
        PrintStream responsePrintStream = new PrintStream(responseByteStream);
        when(res.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int i) throws IOException {
                responseByteStream.write(i);
            }

            public void write(byte[] bytes) throws java.io.IOException {
                responseByteStream
                        .write(bytes);
            }

            public void write(byte[] bytes, int i, int i1) throws java.io.IOException {
                responseByteStream.write(bytes, i, i1);
            }

            public void flush() throws java.io.IOException {
                responseByteStream.flush();
            }

            public void close() throws java.io.IOException {
                responseByteStream.close();
            }
        }
        );
        renderer = FreemarkerRendererMother.create();
    }

    @Test
    public void testRendererIncludesParsedFiles() throws Exception {
        renderer.renderResult(req, res, new ControllerForTesting(), "doit", "parse");
        assertEquals("this is some included content", responseByteStream.toString());
    }

    @Test
    public void testRendererFindsTemplateForExceptionSuperclass() throws Exception {
        renderer.renderException(req, res, new ControllerForTesting(), "doit", new ControllerExceptionSubclassForTesting());
        assertEquals("this is the ControllerExceptionForTesting template", responseByteStream.toString());
    }
}
