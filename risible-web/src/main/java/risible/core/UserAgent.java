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

package risible.core;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class UserAgent {
    private static final Pattern IE = Pattern.compile(".*MSIE.*");
    private static final Pattern IE8 = Pattern.compile(".*MSIE 8.*");
    private static final Pattern IE7 = Pattern.compile(".*MSIE 7.*");
    private static final Pattern IE6 = Pattern.compile(".*MSIE 6.*");
    private static final Pattern IE5 = Pattern.compile(".*MSIE 5.*");
    private static final Pattern FF = Pattern.compile(".*Firefox.*");
    private static final Pattern FF1_5 = Pattern.compile(".*Firefox/1.5.*");
    private static final Pattern FF2 = Pattern.compile(".*Firefox/2.*");
    private static final Pattern FF3 = Pattern.compile(".*Firefox/3.*");
    private static final Pattern CHROME = Pattern.compile(".*Chrome*");
    private static final Pattern SAFARI = Pattern.compile(".*Safari*");
    private static final Pattern OPERA = Pattern.compile(".*Opera*");

    private HttpServletRequest req;

    public UserAgent(HttpServletRequest req) {
        this.req = req;
    }

    public boolean ie() {
        return matches(IE);
    }

    public boolean ff() {
        return matches(FF);
    }

    public boolean ff2() {
        return matches(FF2);
    }

    public boolean opera() {
        return matches(OPERA);
    }

    public boolean safari() {
        return matches(SAFARI);
    }

    public boolean chrome() {
        return matches(CHROME);
    }

    protected boolean matches(Pattern p) {
        String ua = req.getHeader("user-agent");
        if (ua == null) {
            return false;
        }
        return p.matcher(ua).matches();
    }
}
