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

public interface RequestLogger {

    void ignored(HttpServletRequest req, long elapsed);

    void error(HttpServletRequest req, Object controller, String invocationPath, long elapsed, Throwable exception);

    void action(HttpServletRequest req, Object controller, String invocationPath, long elapsed);
}
