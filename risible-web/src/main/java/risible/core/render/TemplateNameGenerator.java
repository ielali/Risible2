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

import org.apache.commons.lang.StringUtils;

import java.util.Iterator;

public abstract class TemplateNameGenerator implements Iterator {
    public static final String EXTENSION = ".ftl";

    protected String packageName;
    protected String controller;
    protected String action;

    public TemplateNameGenerator(Class controller, String action) {
        this.packageName = controller != null ? controller.getPackage().getName().replace('.', '/') : "";
        this.controller = controller != null ? controller.getSimpleName() : "";
        this.action = action;
    }

    public void remove() {
        throw new UnsupportedOperationException("duh");
    }

    public String toString() {
        return packageName + "/" + controller + "." + action;
    }

    public String getTemplateName() {
        for (; hasNext(); ) {
            String name = (String) next();
            if (getClass().getResource("/" + name) != null) {
                return name;
            }
        }
        throw new MissingTemplate("No template found for " + this, previousException());
    }

    protected Throwable previousException() {
        return null;
    }

    protected String controllerUncapitalised() {
        return StringUtils.uncapitalize(controller);
    }
}
