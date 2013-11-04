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

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import risible.core.annotations.Renders;
import risible.core.annotations.UsesInvoker;
import risible.core.log.Log4jRequestLogger;
import risible.core.log.RequestLogger;
import risible.core.MediaType;
import risible.core.render.Renderer;
import risible.freemarker.CookieManager;
import risible.servlet.BlockErrors;
import risible.servlet.MultipartHelper;
import risible.servlet.RequestHelper;
import risible.servlet.SessionMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class Filter implements javax.servlet.Filter, ApplicationContextAware {
    private final Logger log = Logger.getLogger(Filter.class);
    private ConfigurableListableBeanFactory beanFactory;
    private Map<String, Invoker> invokers;
    private Map<MediaType, Renderer> rendererPerMediaType = new Hashtable<MediaType, Renderer>();
    private String actionPackage;
    private RequestLogger logger = new Log4jRequestLogger();
    private List<String> extensions = Arrays.asList(".do", ".ajax");
    private DispatchingStrategy dispatchingStrategy = new ExtensionDispatchingStrategy(extensions);
    private BlockErrors errorIpBlocker = BlockErrors.NULL;

    public Filter() throws IOException {
    }

    public void setActionPackage(String actionPackage) {
        this.actionPackage = actionPackage;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long now = System.currentTimeMillis();
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        if (dispatchingStrategy.shouldDispatch(req)) {
            runAction(req, res, now);
            return;
        }
        filterChain.doFilter(req, servletResponse);
        logger.ignored(req, elapsed(now));
    }

    protected void runAction(HttpServletRequest request, HttpServletResponse response, long start) throws ServletException {
        Throwable error = null;
        Invocation invocation = null;
        Object controller = null;
        try {
            invocation = createInvocation(request);
            controller = createController(invocation);
            setContentType(response, invocation);
            invokeAction(request, response, invocation, controller);
        } catch (NotFound nf) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Throwable t) {
            errorIpBlocker.error(RequestHelper.getForwardedForOrRemoteIp(request));
            error = t;
            wrapException(request, t);
        } finally {
            logRequest(elapsed(start), request, error, controller, invocation);
        }
    }

    private Invocation createInvocation(HttpServletRequest request) throws InvocationFailed {
        String uri = getUri(request);
        String[] path = uri.split("\\/");
        return Invocation.create(actionPackage, path);
    }

    private Object createController(Invocation invocation) {
        return beanFactory.autowire(invocation.getTargetClass(), AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
    }


    private void setContentType(HttpServletResponse servletResponse, Invocation invocation) {
        servletResponse.setContentType(invocation.getMediaType().toString());
    }

    private String getUri(HttpServletRequest request) {
        int contextPathLength = request.getContextPath().length();
        String uri = request.getRequestURI().substring(contextPathLength);
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        return uri;
    }

    private void invokeAction(HttpServletRequest request, HttpServletResponse response, Invocation invocation, Object controller) throws Exception {
        Method action = invocation.getActionMethod();
        Invoker invoker = getInvoker(action);
        Map requestParameters = new TreeMap(request.getParameterMap());
        Map headerParameters = new TreeMap();
        for (Enumeration enumeration = request.getHeaderNames(); enumeration.hasMoreElements(); ) {
            Object key = enumeration.nextElement();
            headerParameters.put(key, request.getHeader(key.toString()));
        }
        requestParameters.putAll(MultipartHelper.parseMulti(request));
        requestParameters.put("request", request);
        requestParameters.put("session", new SessionMap(request));
        requestParameters.put("cookies", new CookieManager(request, response));
        requestParameters.put("responseFormat", invocation.getExtension());
        MediaType mediaType = invocation.getMediaType();
        Renderer renderer = rendererPerMediaType.get(mediaType);
        try {
            renderer.renderResult(request, response, controller, invocation.getMethodName(), invoker.invoke(controller, invocation, requestParameters, headerParameters));
        } catch (NotFound notFound) {
            renderer.renderException(request, response, controller, invocation.getMethodName(), notFound);
        } catch (InvocationFailed failure) {
            Throwable cause = failure.getCause();
            renderer.renderException(request, response, controller, invocation.getMethodName(), cause);
        }
    }

    private Invoker getInvoker(Method action) {
        String invokerName = "default";
        if (action != null && action.getAnnotation(UsesInvoker.class) != null) {
            invokerName = action.getAnnotation(UsesInvoker.class).value();
        }
        return invokers.get(invokerName);
    }

    private void logRequest(long elapsed, HttpServletRequest request, Throwable throwable, Object controller, Invocation invocation) {
        if (throwable == null) {
            logger.action(request, controller, invocation == null ? null : invocation.getInvocationPath(), elapsed);
        } else {
            logger.error(request, controller, invocation == null ? null : invocation.getInvocationPath(), elapsed, throwable);
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

    public void destroy() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
    }

    public void setActionInvokers(Map<String, Invoker> invokers) {
        this.invokers = invokers;
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

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
        dispatchingStrategy = new ExtensionDispatchingStrategy(this.extensions);
    }

    public void setLogger(RequestLogger logger) {
        this.logger = logger;
    }


    public void setDispatchingStrategy(DispatchingStrategy dispatchingStrategy) {
        this.dispatchingStrategy = dispatchingStrategy;
    }

    /**
     * optional - connect this to a Filter that will drop connections from clients that are
     * provoking too many errors.
     *
     * @param errorIpBlocker
     */
    public void setErrorIpBlocker(BlockErrors errorIpBlocker) {
        this.errorIpBlocker = errorIpBlocker;
    }
}
