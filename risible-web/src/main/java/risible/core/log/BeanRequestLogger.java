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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public abstract class BeanRequestLogger extends RequestLoggerSupport {
    public void ignored(HttpServletRequest req, long elapsed) {
        save(populate(newInstance("static"), req, null, null, elapsed, null));
    }

    public void error(HttpServletRequest req, Object controller, String invocationPath, long elapsed, Throwable t) {
        save(populate(newInstance("error"), req, controller, invocationPath, elapsed, t));
    }

    public void action(HttpServletRequest req, Object controller, String invocationPath, long elapsed) {
        save(populate(newInstance("action"), req, controller, invocationPath, elapsed, null));
    }

    protected RequestLogBean newInstance(String type) {
        RequestLogBean bean = new RequestLogBean();
        bean.setLogType(type);
        return bean;
    }

    protected abstract void save(RequestLogBean bean);

    protected RequestLogBean populate(RequestLogBean b, HttpServletRequest req, Object controller, String invocationPath, long elapsed, Throwable t) {
        b.setDateInfo(new Date());
        b.setProblem(t);
        b.setController(controller != null ? controller.getClass().getName() : null);
        b.setInvocation(invocationPath);
        b.setElapsed(elapsed);
        b.setUri(req.getRequestURI());
        b.setQs(getQS(req));
        b.setAcceptLanguage(getAcceptLanguage(req));
        b.setReferrer(req.getHeader("Referer"));
        b.setRemoteIp(getRemoteIp(req));
        b.setUserAgent(req.getHeader("user-agent"));
        return b;
    }

    protected String getAcceptLanguage(HttpServletRequest req) {
        String al = req.getHeader("Accept-Language");
        String[] ll;
        if (al != null) {
            ll = al.split(",");
            if (ll.length > 0) {
                return ll[0];
            }
        }

        return al;
    }
}
