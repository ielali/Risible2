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

import org.apache.commons.lang.StringUtils;
import risible.util.Lists;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;

public class CookieManager {
    private static final int MAX_COOKIE_AGE = 60 * 60 * 24 * 365 * 10;  // 10 Years

    private HttpServletRequest req;
    private HttpServletResponse res;
    public static final String SEPARATOR = "#";

    public CookieManager(HttpServletRequest req, HttpServletResponse res) {
        this.req = req;
        this.res = res;
    }

    public void add(String name, String value) {
        List oldValues = getList(name);
        oldValues.remove(value);
        List result = new LinkedList();
        result.add(value);
        result.addAll(oldValues);
        if (result.size() > 10) {
            result = result.subList(0, 10);
        }
        res.addCookie(createCookie(name, result));
    }

    public void set(String name, String value) {
        res.addCookie(createCookie(name, value));
    }

    private Cookie createCookie(String name, List multipleValues) {
        return createCookie(name, Lists.join(multipleValues, SEPARATOR));
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(MAX_COOKIE_AGE);
        cookie.setPath("/");
        return cookie;
    }

    public List getList(String name) {
        String value = get(name);
        if (StringUtils.isBlank(value)) {
            return EMPTY_LIST;
        }
        return Lists.build(value.split(SEPARATOR));
    }

    public String get(String name) {
        if (req.getCookies() == null) {
            return "";
        }

        for (Cookie cookie : req.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return "";
    }
}
