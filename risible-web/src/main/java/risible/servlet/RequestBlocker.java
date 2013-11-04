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

package risible.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A filter to drop requests for illegal URIs - for example, if someone is requesting /cgi-bin/*, or /login.php on your
 * java app, they're most likely looking for vulnerabilities.
 */
public class RequestBlocker implements Filter {
    private Set<Pattern> patterns = new HashSet();
    private BlockedIpStore ips = new BlockedIpMemoryStore();

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String ip = RequestHelper.getForwardedForOrRemoteIp(req);
        if (isIpBlocked(ip)) {
            blockedIp(ip);
            return;
        }

        String uri = req.getRequestURI();
        for (Pattern pattern : patterns) {
            if (pattern.matcher(uri).matches()) {
                blockedPattern(ip, uri, pattern);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    protected boolean isIpBlocked(String ip) {
        return ips.isBlocked(ip);
    }

    protected void blockedPattern(String ip, String uri, Pattern pattern) {
        blockIp(ip);
    }

    protected void blockedIp(String ip) {
    }

    public void destroy() {
    }

    public void setPatterns(Set<String> patterns) {
        for (String pattern : patterns) {
            addPattern(pattern);
        }
    }

    public void addPattern(String pattern) {
        this.patterns.add(Pattern.compile(pattern));
    }

    public void blockIp(String ip) {
        ips.block(ip);
    }

    public void setBlockedIpStore(BlockedIpStore ips) {
        this.ips = ips;
    }
}
