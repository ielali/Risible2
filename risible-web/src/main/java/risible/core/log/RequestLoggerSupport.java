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

package risible.core.log;

import risible.servlet.RequestHelper;
import risible.util.Lists;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public abstract class RequestLoggerSupport implements RequestLogger {
    private final Set hideParams = new HashSet();

    {
        hideParams.add("password");
    }

    protected String stringify(HttpServletRequest req, Object controller, String invocationPath, long elapsed) {
        return getRemoteIp(req) + " " + req.getMethod() + " " + getUri(req) + " invoked " + invocationPath + " took " + elapsed + "ms";
    }

    protected String getRemoteIp(HttpServletRequest req) {
        return RequestHelper.getForwardedForOrRemoteIp(req);
    }

    protected String getUri(HttpServletRequest req) {
        String qs = getQS(req);
        return req.getRequestURL() + (qs.length() == 0 ? "" : "?" + qs);
    }

    protected String getQS(HttpServletRequest req) {
        List kvp = new LinkedList();
        Map p = req.getParameterMap();
        for (Object k : p.keySet()) {
            if (!hideParams.contains(k)) {
                Object[] v = (Object[]) p.get(k);
                for (Object o : v) {
                    kvp.add(k + "=" + o);
                }
            }
        }
        return Lists.join(kvp, "&");
    }

    public void setHideParams(Set hideParams) {
        this.hideParams.clear();
        this.hideParams.addAll(hideParams);
    }
}
