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

import freemarker.ext.beans.StringModel;
import freemarker.template.*;
import risible.util.Pair;
import risible.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static risible.util.Maps.stringify;

public class LinkToMethod implements TemplateMethodModelEx {
    public Object exec(List arguments) throws TemplateModelException {
        String linkText = ((SimpleScalar) arguments.get(0)).getAsString();
        HttpServletRequest request = (HttpServletRequest) ((StringModel) arguments.get(1)).getWrappedObject();
        Map<String, List<String>> newParams = mapify((TemplateHashModelEx) arguments.get(2));
        Map options = getOptions(arguments);

        if (paramsAreDifferent(request, newParams)) {
            return generateHtml(request, newParams, linkText, options);
        } else {
            return generateBold(linkText);
        }
    }

    private Map getOptions(List arguments) throws TemplateModelException {
        if (arguments.size() > 3) {
            TemplateHashModelEx hash = (TemplateHashModelEx) arguments.get(3);
            Map result = new HashMap();
            for (TemplateModelIterator i = hash.keys().iterator(); i.hasNext(); ) {
                String key = i.next().toString();
                String value = hash.get(key).toString();
                result.put(key, value);
            }
            return result;
        } else {
            return Collections.EMPTY_MAP;
        }
    }

    private boolean paramsAreDifferent(HttpServletRequest request, Map<String, List<String>> newParams) {
        String params = request.getQueryString();
        Map map = mapify(params);
        String oldQueryString = stringify(map);

        map.putAll(newParams);
        String newQueryString = stringify(map);

        return !oldQueryString.equals(newQueryString);
    }

    private String generateBold(String linkText) {
        return "<b>" + linkText + "</b>";
    }

    private String generateHtml(HttpServletRequest request, Map<String, List<String>> newParams, String linkText, Map options) {
        String idText = "";
        if (options.containsKey("id")) {
            idText = "id='" + options.get("id") + "'";
        }
        String targetUrl = createRequest(request, newParams);
        if (veto(targetUrl)) {
            return "";
        } else {
            return "<a " + idText + " href='" + targetUrl + "'>" + linkText + "</a>";
        }
    }

    protected boolean veto(String targetUrl) {
        return false;
    }

    private Map<String, List<String>> mapify(TemplateHashModelEx params) throws TemplateModelException {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (TemplateModelIterator i = params.keys().iterator(); i.hasNext(); ) {
            TemplateScalarModel key = (TemplateScalarModel) i.next();
            SimpleSequence values = (SimpleSequence) params.get(key.getAsString());
            List<String> newValues = new LinkedList<String>();
            for (Object o : values.toList()) {
                newValues.add(o.toString());
            }
            map.put(key.getAsString(), newValues);
        }
        return map;
    }

    private String createRequest(HttpServletRequest request, Map<String, List<String>> newParams) {
        String url = request.getRequestURL().toString();
        String params = mergeParams(request.getQueryString(), newParams);
        return url + "?" + params;
    }

    private String mergeParams(String params, Map<String, List<String>> newParams) {
        Map map = mapify(params);
        map.putAll(newParams);
        params = stringify(map);
        return params;
    }

    private Map<String, List<String>> mapify(String params) {
        Map<String, List<String>> map = new TreeMap<String, List<String>>();
        if (params == null) {
            return map;
        }

        for (Pair nv : Strings.parseQueryString(params)) {
            addToMap(map, nv.name, nv.value);
        }
        return map;
    }

    private void addToMap(Map<String, List<String>> map, String key, String value) {
        List<String> list = map.get(key);
        if (list == null) {
            list = new LinkedList<String>();
            map.put(key, list);
        }
        list.add(value);
    }

}
