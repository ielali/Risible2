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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class UrlEncodeMethod implements TemplateMethodModel {
    public Object exec(List arguments) throws TemplateModelException {
        String text = (String) arguments.get(0);
        try {
            return urlEncode(text);
        } catch (UnsupportedEncodingException e) {
            throw new TemplateModelException("Unable to encode " + text, e);
        }
    }

    public static String urlEncode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }
}
