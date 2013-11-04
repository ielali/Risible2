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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import risible.util.Lists;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import static org.mockito.Mockito.*;

public class RequestBlockerTest {
    private RequestBlocker requestBlocker;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private FilterChain filterChain;

    @Before
    public void setUp() {
        req = Mockito.mock(HttpServletRequest.class);
        res = Mockito.mock(HttpServletResponse.class);
        filterChain = Mockito.mock(FilterChain.class);
        Set toBlock = Lists.buildSet(".*\\.php");
        requestBlocker = new RequestBlocker();
        requestBlocker.setPatterns(toBlock);
    }

    @Test
    public void testPassesHtmlRequests() throws IOException, ServletException {
        checkUri("/editor/edit.html");
        verify(filterChain, times(1)).doFilter(req, res);
    }

    @Test
    public void testBlocksPhpRequests() throws IOException, ServletException {
        checkUri("/mantis/login_page.php");
    }

    private void checkUri(String uri) throws IOException, ServletException {
        when(req.getRequestURL()).thenReturn(new StringBuffer(uri));
        when(req.getRequestURI()).thenReturn(uri);
        requestBlocker.doFilter(req, res, filterChain);
    }
}
