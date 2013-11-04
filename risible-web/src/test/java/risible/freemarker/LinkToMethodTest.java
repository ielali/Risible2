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

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import risible.util.Lists;
import risible.util.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class LinkToMethodTest {

    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse res;

    public void testCreatesUrlForNullQueryString() throws TemplateModelException {
        when(req.getQueryString()).thenReturn(null);
        Map newParams = Maps.make("a", Lists.build("z"), "e", Lists.build("f"));
        checkRequest(req, newParams, "<a id='id_for_link' href='http://foo.com:80/bar/toto?a=z&e=f'>text</a>", true);
    }

    public void testCreatesUrlForSimpleCase() throws TemplateModelException {
        when(req.getQueryString()).thenReturn("a=b&c=d");
        Map newParams = Maps.make("a", Lists.build("z"), "e", Lists.build("f"));
        checkRequest(req, newParams, "<a id='id_for_link' href='http://foo.com:80/bar/toto?a=z&c=d&e=f'>text</a>", true);
    }

    public void testRendersBlankIfNotAllowed() throws TemplateModelException {
        when(req.getQueryString()).thenReturn("a=b&c=d");
        Map newParams = Maps.make("a", Lists.build("z"), "e", Lists.build("f"));
        checkRequest(req, newParams, "", false);
    }

    public void testCreatesNonLinkIfUrlIsNotChanged() throws TemplateModelException {
        when(req.getQueryString()).thenReturn("a=b&c=d");
        Map newParams = Maps.make("a", Lists.build("b"));
        checkRequest(req, newParams, "<b>text</b>", true);
    }

    public void testCreatesUrlForComplexCase() throws TemplateModelException {
        when(req.getQueryString()).thenReturn("a=b&c=d&a=g&e=h");
        Map newParams = Maps.make("a", Lists.build("z"), "e", Lists.build("f"));
        checkRequest(req, newParams, "<a id='id_for_link' href='http://foo.com:80/bar/toto?a=z&c=d&e=f'>text</a>", true);
    }

    public void testCreatesUrlForMultipleExpectedParameters() throws TemplateModelException {
        when(req.getQueryString()).thenReturn("a=b&c=d&a=g&e=h");
        Map newParams = Maps.make("a", Lists.build("x", "y", "z"), "e", Lists.build("f", "g"));
        checkRequest(req, newParams, "<a id='id_for_link' href='http://foo.com:80/bar/toto?a=x&a=y&a=z&c=d&e=f&e=g'>text</a>", true);
    }

    public void testOmitsNullParameters() throws TemplateModelException {
        when(req.getQueryString()).thenReturn("a=b&c=&a=&e=h&x=&y=&z=&x=");
        Map newParams = Maps.make("a", Lists.build("x", "y", "z"), "e", Lists.build("f", "g"));
        checkRequest(req, newParams, "<a id='id_for_link' href='http://foo.com:80/bar/toto?a=x&a=y&a=z&e=f&e=g'>text</a>", true);
    }

    private void checkRequest(HttpServletRequest req, Map newParams, String expected, final boolean allowUrl) throws TemplateModelException {
        StringModel request = new StringModel(req, (BeansWrapper) BeansWrapper.BEANS_WRAPPER);
        SimpleScalar text = new SimpleScalar("text");
        LinkToMethod method = new LinkToMethod() {
            protected boolean veto(String targetUrl) {
                return !allowUrl;
            }
        };
        String actual = (String) method.exec(Lists.build(text, request, wrap(newParams), buildOptions()));
        Assert.assertEquals(expected, actual);
    }

    private SimpleHash buildOptions() {
        Map options = new HashMap();
        options.put("id", "id_for_link");
        return new SimpleHash(options);
    }

    private SimpleHash wrap(Map<String, List<String>> newParams) {
        SimpleHash result = new SimpleHash();
        for (Map.Entry<String, List<String>> entry : newParams.entrySet()) {
            result.put(entry.getKey(), new SimpleSequence(entry.getValue()));
        }
        return result;
    }

    @Before
    public void createRequest() {
        when(req.getProtocol()).thenReturn("http");
        when(req.getServerName()).thenReturn("foo.com");
        when(req.getRequestURI()).thenReturn("/bar/toto");
    }
}
