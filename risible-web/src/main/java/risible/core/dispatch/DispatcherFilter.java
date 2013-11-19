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

package risible.core.dispatch;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import risible.core.MediaType;
import risible.core.UserAgent;
import risible.core.annotations.HeaderParam;
import risible.core.annotations.QueryParam;
import risible.core.annotations.Renders;
import risible.core.log.Log4jRequestLogger;
import risible.core.log.RequestLogger;
import risible.core.render.Renderer;
import risible.core.render.RendererContext;
import risible.freemarker.CookieManager;
import risible.servlet.SessionMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.*;

public class DispatcherFilter implements javax.servlet.Filter {
    public static final String REDIRECT_RESULT_PREFIX = "redirect";
    private final Logger log = Logger.getLogger(DispatcherFilter.class);
    private Dispatcher dispatcher;
    private RequestLogger logger = new Log4jRequestLogger();
    private Map<MediaType, Renderer> rendererPerMediaType = new Hashtable<MediaType, Renderer>();

    public DispatcherFilter() throws IOException {
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setRenderers(List<Renderer> renderers) {
        if (renderers != null) {
            for (Renderer renderer : renderers) {
                if (renderer.getClass().isAnnotationPresent(Renders.class)) {
                    String mediaTypeValue = renderer.getClass().getAnnotation(Renders.class).value();
                    MediaType mediaType = MediaType.valueOf(mediaTypeValue);
                    if (mediaType != null) {
                        rendererPerMediaType.put(mediaType, renderer);
                    } else {
                        log.warn(String.format("Renderer [%s] has invalid MediaType annotation value[%s]",
                                renderer.getClass().getName(), mediaType));
                    }
                } else {
                    log.warn(String.format("Renderer [%s] has no MediaType annotation", renderer.getClass().getName()));
                }
            }
        }
    }

    public void setLogger(RequestLogger logger) {
        this.logger = logger;
    }


    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long now = System.currentTimeMillis();
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        runAction(req, res, now);
        logger.ignored(req, elapsed(now));
    }


    public void destroy() {
    }

    protected void runAction(HttpServletRequest request, HttpServletResponse response, long start) throws ServletException {
        Throwable error = null;
        DispatcherResult dispatcherResult = null;
        try {
            dispatcherResult = dispatcher.dispatch(getDispatcherContext(request));
            render(request, response, dispatcherResult);
        } catch (Throwable throwable) {
            error = throwable;
            wrapException(request, throwable);
        } finally {
            logRequest(elapsed(start), request, error, dispatcherResult != null ? dispatcherResult.getController() : null);
        }
    }

    private void render(HttpServletRequest request, HttpServletResponse response, DispatcherResult dispatcherResult) throws Exception {
        response.setContentType(dispatcherResult.getMediaType().toString());
        //Redirect response if specifed by controller
        String invocationResult = (dispatcherResult.getResult() instanceof String) ? (String) dispatcherResult.getResult() : "";
        if (StringUtils.isNotBlank(invocationResult)) {
            String[] parts = invocationResult.split(":");
            if (parts.length == 2 && parts[0].equals(REDIRECT_RESULT_PREFIX)) {
                String requestURI = request.getRequestURI();
                response.sendRedirect(requestURI.substring(0, requestURI.indexOf(dispatcherResult.getAction())) + parts[1]);
                return;
            }
        }
        Renderer renderer = rendererPerMediaType.get(dispatcherResult.getMediaType());
        renderer.render(getRendererContext(request, response, dispatcherResult));
    }

    private DispatcherContext getDispatcherContext(HttpServletRequest request) {
        TreeMap requestParameters = new TreeMap(request.getParameterMap());
        TreeMap headerParameters = new TreeMap();
        for (Enumeration enumeration = request.getHeaderNames(); enumeration.hasMoreElements(); ) {
            Object key = enumeration.nextElement();
            headerParameters.put(key, request.getHeader(key.toString()));
        }
        Map<Class<? extends Annotation>, TreeMap<String, Object>> parameters = new HashMap<Class<? extends Annotation>, TreeMap<String, Object>>();
        parameters.put(QueryParam.class, requestParameters);
        parameters.put(HeaderParam.class, headerParameters);
        return new DispatcherContext(getUri(request), parameters);
    }

    private RendererContext getRendererContext(HttpServletRequest request, HttpServletResponse response, DispatcherResult result) throws IOException {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("params", request.getParameterMap());
        context.put("contextPath", request.getContextPath());
        context.put("userAgent", new UserAgent(request));
        context.put("session", new SessionMap(request));
        context.put("cookies", new CookieManager(request, response));
        if (result.getThrowable() != null) {
            context.put("error", result.getThrowable());
        }
        return new RendererContext(result.getController(), result.getAction(), result.getResult(), result.getThrowable(), context, createWriter(response));
    }

    private String getUri(HttpServletRequest request) {
        int contextPathLength = request.getContextPath().length();
        String uri = request.getRequestURI().substring(contextPathLength);
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        return uri;
    }

    private Writer createWriter(ServletResponse res) throws IOException {
        return new OutputStreamWriter(res.getOutputStream());
    }

    private void logRequest(long elapsed, HttpServletRequest request, Throwable throwable, Object controller) {
        if (throwable == null) {
            logger.action(request, controller, getUri(request), elapsed);
        } else {
            logger.error(request, controller, getUri(request), elapsed, throwable);
        }
    }

    private long elapsed(long now) {
        return System.currentTimeMillis() - now;
    }

    protected void wrapException(HttpServletRequest request, Throwable e) throws ServletException {
        ServletException servletException = new ServletException("Requesting " + request.getRequestURL(), e);
        if (servletException.getCause() == null) { // some servlet container implementations don't assign the cause from the constructor
            throw (ServletException) servletException.initCause(e);
        } else {
            throw servletException;
        }
    }

}
