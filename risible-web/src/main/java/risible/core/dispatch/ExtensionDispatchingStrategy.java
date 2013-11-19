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

package risible.core.dispatch;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ExtensionDispatchingStrategy implements DispatchingStrategy {
    private final List<String> allowedExtensions;

    public ExtensionDispatchingStrategy(List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public boolean shouldDispatch(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        if ("/".equals(servletPath) || servletPath.length() == 0) {
            return false;
        }
        for (String extension : allowedExtensions) {
            if (servletPath.contains(extension)) {
                return true;
            }
        }
        return false;
    }

}
