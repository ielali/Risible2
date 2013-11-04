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

public class ResultTemplateNameGenerator extends TemplateNameGenerator {
    private static final Map<String, String> cache = new HashMap();
    private String result;
    private int step = 0;

    public static String lookup(Class controller, String action, String result) {
        String key = controller.getName() + "-" + action + "-" + result;
        String templateName = cache.get(key);
        if (templateName == null) {
            templateName = new ResultTemplateNameGenerator(controller, action, result).getTemplateName();
            cache.put(key, templateName);
        }
        return templateName;
    }

    public ResultTemplateNameGenerator(Class controller, String action, String result) {
        super(controller, action);
        if (result == null) {
            step = 6;
        }
        this.result = result;
    }

    public boolean hasNext() {
        return step < 10;
    }

    public Object next() {
        String name = null;
        switch (step) {
            case 0:
                name = packageName + "/" + controller + "-" + action + "-" + result + EXTENSION;
                break;
            case 1:
                name = packageName + "/" + controllerUncapitalised() + "/" + action + "-" + result + EXTENSION;
                break;
            case 2:
                name = packageName + "/" + controller + "-" + result + EXTENSION;
                break;
            case 3:
                name = packageName + "/" + controllerUncapitalised() + "/" + result + EXTENSION;
                break;
            case 4:
                name = packageName + "/" + action + "-" + result + EXTENSION;
                break;
            case 5:
                name = packageName + "/" + result + EXTENSION;
                break;
            case 6:
                name = packageName + "/" + action + EXTENSION;
                break;
            case 7:
                name = packageName + "/" + controller + "-" + action + EXTENSION;
                break;
            case 8:
                name = packageName + "/" + controllerUncapitalised() + "/" + action + EXTENSION;
                break;
            case 9:
                name = packageName + "/" + controller + EXTENSION;
        }
        step++;
        return name;
    }

    public String toString() {
        return super.toString() + " for action result " + result;
    }
}
