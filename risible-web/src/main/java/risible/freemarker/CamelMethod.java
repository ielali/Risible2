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

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import risible.util.TextHelper;

import java.util.LinkedList;
import java.util.List;

public class CamelMethod implements TemplateMethodModelEx {
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.get(0) instanceof TemplateSequenceModel) {
            TemplateSequenceModel list = (TemplateSequenceModel) arguments.get(0);
            List result = new LinkedList();
            for (int i = 0; i < list.size(); i++) {
                result.add(camel(list.get(i).toString()));
            }
            return result;
        } else {
            return camel(arguments.get(0).toString());
        }
    }

    private static String camel(String text) {
        return TextHelper.extendedCamel(text);
    }
}
