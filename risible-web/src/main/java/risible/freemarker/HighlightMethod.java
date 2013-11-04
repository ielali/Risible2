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

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.StringUtils;
import risible.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighlightMethod implements TemplateMethodModel {
    private static final String BEFORE_TERM = "(^|\\W)";
    private static final String AFTER_TERM = "(\\W|$)";
    private static final Pattern TAG = Pattern.compile("(<[^>]*>)");

    public Object exec(List arguments) throws TemplateModelException {
        String searchTerm = getSearchExpression();
        if (searchTerm == null || searchTerm.length() == 0) {
            return arguments.get(0);
        } else {
            return highlight((String) arguments.get(0), Strings.stripNonWords(searchTerm).split(" "));
        }
    }

    private String highlight(String text, String[] searchTerms) {
        Map careful = new HashMap();
        text = saveTags(text, careful);

        for (String searchTerm : searchTerms) {
            String regex;
            if (StringUtils.isNumeric(searchTerm) || searchTerm.length() < 3) {
                regex = BEFORE_TERM + "((?i:" + searchTerm + "))" + AFTER_TERM;
            } else {
                regex = BEFORE_TERM + "([a-zA-Z0-9]*(?i:" + searchTerm + ")[a-zA-Z0-9]*)()";
            }
            text = text.replaceAll(regex, "$1@<@$2@>@$3");
        }

        return restoreTags(insertHighlightSpan(text), careful);
    }

    private String saveTags(String text, Map careful) {
        Matcher m = TAG.matcher(text);
        StringBuffer sb = new StringBuffer();
        String wow = "!";
        while (m.find()) {
            String token = "@@@" + wow + "@@@";

            String mGroup = m.group();
            mGroup = replaceString(mGroup, "$", "___dollar___");

            careful.put(token, mGroup);
            m.appendReplacement(sb, token);
            wow += "!";
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String replaceString(String mGroup, String toReplace, String replacement) {
        StringBuffer sb = new StringBuffer("");

        int prevIndex = 0;
        for (int index = 0; (index = mGroup.indexOf(toReplace, prevIndex)) != -1; prevIndex = index + toReplace.length()) {
            sb.append(mGroup.substring(prevIndex, index));
            sb.append(replacement);
        }

        sb.append(mGroup.substring(prevIndex));

        return sb.toString();
    }

    private String restoreTags(String text, Map<String, String> careful) {
        for (String token : careful.keySet()) {
            text = text.replaceAll(token, careful.get(token));
            text = replaceString(text, "___dollar___", "$");
        }
        return text;
    }

    private String insertHighlightSpan(String text) {
        return text.replaceAll("<@@", "").replaceAll("@@>", "").replaceAll("@<@", "<span class=\"highlight\">").replaceAll("@>@", "</span>");
    }

    protected String getSearchExpression() throws TemplateModelException {
        Environment e = Environment.getCurrentEnvironment();
        StringModel stringModel = (StringModel) e.getDataModel().get("req");
        HttpServletRequest req = (HttpServletRequest) stringModel.getWrappedObject();
        return req.getParameter("searchExpression");
    }
}
