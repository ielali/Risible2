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

import freemarker.cache.TemplateLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

public class ClasspathTemplateLoader implements TemplateLoader {
    public Object findTemplateSource(String name) {
        URL url = getClass().getResource("/" + name);
        if (url == null) {
            return null;
        }
        File file = new File(url.getFile()).getAbsoluteFile();
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    public long getLastModified(Object templateSource) {
        if (templateSource instanceof File) {
            File f = (File) templateSource;
            return f.lastModified();
        }
        return 0;
    }

    public Reader getReader(Object templateSource, String encoding) throws IOException {
        if (templateSource instanceof File) {
            return new FileReader((File) templateSource);
        }
        return null;
    }

    public void closeTemplateSource(Object templateSource) {
    }
}
