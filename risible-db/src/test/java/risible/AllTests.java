// Copyright 2008 Conan Dalton and Jean-Philippe Hallot
//
// This file is part of risible-db.
//
// risible-db is free software: you can redistribute it and/or modify
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

package risible;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;

public class AllTests extends TestSuite {
    public static TestSuite suite() throws Throwable {
        TestSuite suite = new TestSuite();
        addTests(suite, "./src/test/", "**/*Test.java", null);
        suite.setName("Micro Tests (" + suite.countTestCases() + " tests)");
        return suite;
    }

    public static void addTests(TestSuite suite, String sourceRoot, String filesToInclude, String filesToExclude) throws ClassNotFoundException {
        File projectRoot = new File(sourceRoot);
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(projectRoot);
        final String[] includedTests = new String[]{filesToInclude};
        scanner.setIncludes(includedTests);
        if (filesToExclude != null) {
            scanner.setExcludes(new String[]{filesToExclude});
        }
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        for (int i = 0; i < files.length; i++) {
            String s = files[i];
            final String className = s.substring(0, s.length() - 5).replace(File.separatorChar, '.');
            suite.addTestSuite((Class<? extends TestCase>) Class.forName(className));
        }
    }
}
