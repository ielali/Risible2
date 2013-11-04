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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class RememberFilterTest {
    private HttpServletRequest req;
    private HttpServletResponse res;

    @Before
    public void setUp() throws Exception {
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
    }

    @Test
    public void testRememberExecutesTodosAfterFiltering() throws IOException, ServletException, InterruptedException {

        RememberFilter remember = new RememberFilter();

        Runnable todo = addToDo(remember);
        Runnable todo2 = addToDoFromAnotherThread(remember);

        new SimpleFilterChain(remember).doFilter(req, res);
        verify(todo, times(1)).run();

    }

    private Runnable addToDo(RememberFilter remember) {
        Runnable todo = mock(Runnable.class);
        remember.todo(todo);
        return todo;
    }

    private Runnable addToDoFromAnotherThread(final RememberFilter remember) throws InterruptedException {
        final Runnable todoFromAnotherThread = mock(Runnable.class);

        Thread forNotDoing = new Thread() {
            public void run() {
                remember.todo(todoFromAnotherThread);
            }
        };
        forNotDoing.start();
        forNotDoing.join();
        return todoFromAnotherThread;
    }
}
