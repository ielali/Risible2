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
import org.apache.log4j.Logger;
import risible.core.MediaType;
import risible.core.annotations.Renders;
import risible.core.render.ExceptionTemplateNameGenerator;
import risible.core.render.Renderer;
import risible.core.render.RendererContext;
import risible.core.render.ResultTemplateNameGenerator;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

@Named("freemarkerRenderer")
@Renders(MediaType.TEXT_HTML)
public class FreemarkerRenderer implements Renderer {
    private static final Logger log = Logger.getLogger(FreemarkerRenderer.class);

    private Configuration configuration;

    @Inject
    public void setFreemarkerConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void render(RendererContext rendererContext) throws Exception {
        Object templateContext = createTemplateContext(rendererContext);
        String templateName = null;
        Class<?> controllerClass = rendererContext.getController() != null ? rendererContext.getController().getClass() : null;
        if (rendererContext.getThrowable() == null) {
            templateName = ResultTemplateNameGenerator.lookup(controllerClass, rendererContext.getAction(), (String) rendererContext.getResult());
        } else {
            templateName = ExceptionTemplateNameGenerator.lookup(rendererContext.getThrowable(), controllerClass, rendererContext.getAction());
        }
        try {
            renderTemplate(templateName, templateContext, rendererContext.getWriter());
        } catch (EOFException eof) {
        } catch (Throwable t) {
            log.error("Error rendering template " + templateName, t);
            throw new Error(t);
        }

    }

    protected void renderTemplate(String templateName, Object context, Writer writer) throws IOException, TemplateException {
        Template t = configuration.getTemplate(templateName);
        t.process(context, writer);
        writer.flush();
    }

    protected TemplateHashModel createTemplateContext(RendererContext rendererContext) throws TemplateModelException {
        TemplateHashModel action = new BeanModel(rendererContext.getController(), new SeriousBeanModelObjectWrapper());
        SimpleHash context = new InheritingMap(action);
        context.put("action", action);
        for (String key : rendererContext.getRenderingParameters().keySet()) {
            context.put(key, rendererContext.getRenderingParameters().get(key));

        }
        return context;
    }

}
