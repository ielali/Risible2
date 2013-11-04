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

import java.io.File;

public class DataDirectoryProvider {
    private String root;

    public File getRootDirectoryForIndex() {
        return getDirectory("index");
    }

    public File getRootDirectoryForLogs() {
        return getDirectory("logs");
    }

    public void setRoot(String root) {
        this.root = root;
    }

    private File getDirectory(String directoryName) {
        return ensureExists(new File(root, directoryName));
    }

    private File ensureExists(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }
}
