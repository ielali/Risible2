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

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.WordUtils;

import java.util.List;

public class NiceMethod implements TemplateMethodModel {
    private Iterable<String> preserveCaps;
    private Iterable<String> skipNice;

    public Object exec(List arguments) throws TemplateModelException {
        String arg = (String) arguments.get(0);
        for (String regex : skipNice) {
            if (arg.matches(regex)) {
                return arg;
            }
        }

        String result = WordUtils.capitalizeFully(arg, new char[]{' ', ',', '\t', '(', '/', '-', '\''});
        for (String acronym : preserveCaps) {
            result = result.replaceAll(WordUtils.capitalizeFully(acronym) + "(\\b)", acronym);
        }
        return result;
    }

    public void setPreserveCaps(Iterable<String> preserveCaps) {
        this.preserveCaps = preserveCaps;
    }

    public void setSkipNice(Iterable<String> skipNice) {
        this.skipNice = skipNice;
    }
}

