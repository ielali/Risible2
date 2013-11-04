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
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import risible.util.Lists;

import java.util.LinkedList;
import java.util.List;

public class JoinMethod implements TemplateMethodModelEx {
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() == 2 && (arguments.get(1) instanceof TemplateSequenceModel)) {
            TemplateSequenceModel list = (TemplateSequenceModel) arguments.get(1);
            return Lists.join(convertToList(list), arguments.get(0).toString());
        } else {
            List things = arguments.subList(1, arguments.size());
            return Lists.join(eachToString(things), arguments.get(0).toString());
        }
    }

    private List eachToString(List<TemplateScalarModel> things) throws TemplateModelException {
        List result = new LinkedList();
        for (TemplateScalarModel scalar : things) {
            result.add(scalar.getAsString());
        }
        return result;
    }

    private Iterable convertToList(TemplateSequenceModel list) throws TemplateModelException {
        List result = new LinkedList();
        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i));
        }
        return result;
    }
}
