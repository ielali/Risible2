// Copyright 2009 Conan Dalton and Jean-Philippe Hallot
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

import freemarker.template.TemplateException;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import risible.core.dispatch.Filter;
import risible.core.dispatch.Invoker;
import risible.core.log.RequestLogger;
import risible.core.render.Renderer;
import risible.freemarker.FreemarkerRendererMother;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestableFilter {
    protected HttpServletRequest req;
    protected HttpServletResponse res;
    FilterChain chain;
    ConfigurableListableBeanFactory spring;
    ConfigurableApplicationContext appContext;
    Invoker invoker;
    Invoker otherInvoker;
    Map params = new HashMap();

    @Before
    public void setUp() throws NoSuchMethodException {
        req = mock(HttpServletRequest.class);
        res = Mockito.mock(HttpServletResponse.class);
        when(req.getContextPath()).thenReturn("");
        when(req.getServletPath()).thenReturn("yadda yadda");
        when(req.getParameterMap()).thenReturn(params);
        when(req.getMethod()).thenReturn("GET");

        chain = mock(FilterChain.class);

        spring = mock(ConfigurableListableBeanFactory.class);
        appContext = mock(ConfigurableApplicationContext.class);
        when(appContext.getBeanFactory()).thenReturn(spring);

        invoker = mock(Invoker.class);
        otherInvoker = mock(Invoker.class);
    }

    protected Filter prepare(Filter filter) throws TemplateException, IOException, NoSuchMethodException, ServletException {
        filter.setApplicationContext((ApplicationContext) appContext);
        filter.setActionPackage(getClass().getPackage().getName());
        Map invokers = new HashMap();
        invokers.put("default", invoker);
        invokers.put("other", otherInvoker);
        filter.setActionInvokers(invokers);
        ArrayList<Renderer> renderers = new ArrayList<Renderer>(1);
        renderers.add(FreemarkerRendererMother.create());
        filter.setRenderers(renderers);
        filter.setLogger(new NullRequestLogger());
        return filter;
    }

    private static class NullRequestLogger implements RequestLogger {
        public void ignored(HttpServletRequest req, long elapsed) {
        }

        public void error(HttpServletRequest req, Object controller, String invocationPath, long elapsed, Throwable exception) {
        }

        public void action(HttpServletRequest req, Object controller, String invocationPath, long elapsed) {
        }
    }

}
