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

package risible.core.render;

import java.util.HashMap;
import java.util.Map;

public class ExceptionTemplateNameGenerator extends TemplateNameGenerator {
    private static final Map<String, String> cache = new HashMap();
    Class exceptionClass;
    private Throwable originalException;
    int step = 0;

    public static String lookup(Throwable exception, Class controller, String action) {
        String key = controller.getName() + "-" + action + "-" + exception.getClass().getName();
        String templateName = cache.get(key);
        if (templateName == null) {
            templateName = new ExceptionTemplateNameGenerator(exception, controller, action).getTemplateName();
            cache.put(key, templateName);
        }
        return templateName;
    }

    public ExceptionTemplateNameGenerator(Throwable exception, Class controller, String action) {
        super(controller, action);
        this.exceptionClass = exception.getClass();
        this.originalException = exception;
    }

    public boolean hasNext() {
        return exceptionClass != null;
    }

    public Object next() {
        String result = getTemplateName(exceptionClass.getSimpleName());
        if (step == 0) {
            exceptionClass = exceptionClass.getSuperclass();
        }
        return result;
    }

    private String getTemplateName(String exceptionClassName) {
        String name = null;
        switch (step) {
            case 0:
                name = packageName + "/" + controller + "-" + action + "-" + exceptionClassName + EXTENSION;
                break;
            case 1:
                name = packageName + "/" + controllerUncapitalised() + "/" + action + "-" + exceptionClassName + EXTENSION;
                break;
            case 2:
                name = packageName + "/" + controller + "-" + exceptionClassName + EXTENSION;
                break;
            case 3:
                name = packageName + "/" + controllerUncapitalised() + "/" + exceptionClassName + EXTENSION;
                break;
            case 4:
                name = packageName + "/" + exceptionClassName + EXTENSION;
        }
        step++;
        step %= 5;
        return name;
    }

    protected Throwable previousException() {
        return originalException;
    }

    public String toString() {
        return super.toString() + " for exception " + originalException;
    }
}
