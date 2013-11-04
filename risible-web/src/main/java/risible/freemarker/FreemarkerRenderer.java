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

import freemarker.ext.beans.BeanModel;
import freemarker.template.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import risible.core.UserAgent;
import risible.core.render.ExceptionTemplateNameGenerator;
import risible.core.MediaType;
import risible.core.dispatch.NotFound;
import risible.core.render.Renderer;
import risible.core.annotations.Renders;
import risible.core.render.ResultTemplateNameGenerator;
import risible.servlet.SessionMap;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

@Renders(MediaType.TEXT_HTML)
public class FreemarkerRenderer implements Renderer {
    private static final Logger log = Logger.getLogger(FreemarkerRenderer.class);

    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    private Configuration configuration;

    public void renderResult(HttpServletRequest req, HttpServletResponse res, Object controller, String action, Object result) throws Exception {
        String invocationResult = (result instanceof String) ? (String) result : "";
        if (StringUtils.isNotBlank(invocationResult)) {
            String[] parts = invocationResult.split(":");
            if (parts.length == 2 && parts[0].equals("redirect")) {
                String requestURI = req.getRequestURI();
                res.sendRedirect(requestURI.substring(0, requestURI.indexOf(action)) + parts[1]);
                return;
            }
        }
        renderTemplate(req, res, controller, null, ResultTemplateNameGenerator.lookup(controller.getClass(), action, invocationResult));
    }

    public void renderException(HttpServletRequest req, HttpServletResponse res, Object controller, String action, Throwable cause) throws Exception {
        if (cause instanceof NotFound) {
            res.setStatus(404);
        }
        String name = ExceptionTemplateNameGenerator.lookup(cause, controller.getClass(), action);
        renderTemplate(req, res, controller, cause, name);
    }

    private void renderTemplate(HttpServletRequest req, HttpServletResponse res, Object controller, Throwable cause, String templateName) throws Exception {
        Object context = createContext(req, res, controller, cause);
        Writer writer = createWriter(res);
        try {
            renderTemplate(templateName, context, writer);
        } catch (EOFException eof) {
        } catch (Throwable t) {
            log.error("Error rendering template " + templateName, t);
            throw new Error(t);
        }
    }

    public void renderTemplate(String templateName, Object context, Writer writer) throws IOException, TemplateException {
        Template t = configuration.getTemplate(templateName);
        t.process(context, writer);
        writer.flush();
    }

    private Writer createWriter(ServletResponse res) throws IOException {
        return new OutputStreamWriter(res.getOutputStream());
    }

    protected Object createContext(HttpServletRequest req, HttpServletResponse res, Object controller, Throwable error) throws TemplateModelException {
        TemplateHashModel action = new BeanModel(controller, new SeriousBeanModelObjectWrapper());
        SimpleHash context = new InheritingMap(action);
        context.put("params", req.getParameterMap());
        context.put("req", req);
        context.put("res", res);
        context.put("ua", new UserAgent(req));
        context.put("session", new SessionMap(req));
        context.put("action", action);
        context.put("cookies", new CookieManager(req, res));
        if (error != null) {
            context.put("error", error);
        }
        return context;
    }

    public void setFreemarkerConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
