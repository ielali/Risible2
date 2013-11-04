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

import freemarker.template.TemplateModelException;
import junit.framework.TestCase;
import risible.util.Lists;

public class NiceMethodTest extends TestCase {
    private NiceMethod method;

    protected void setUp() throws Exception {
        super.setUp();
        method = new NiceMethod();
        method.setPreserveCaps(Lists.build("BFI", "NCO", "INC", "SA"));
        method.setSkipNice(Lists.build("DNU.*", "DO NOT USE.*"));
    }

    public void testSkipsStringsBeginningWithDNU() throws TemplateModelException {
        assertEquals("DNU SEE BNPA MAD", nice("DNU SEE BNPA MAD"));
    }

    public void testSkipsStringsBeginningWithDO_NOT_USE() throws TemplateModelException {
        assertEquals("DO NOT USE SEE BNPA MAD", nice("DO NOT USE SEE BNPA MAD"));
    }

    public void testSkipsDO_NOT_USE() throws TemplateModelException {
        assertEquals("DO NOT USE", nice("DO NOT USE"));
    }

    public void testCapitalisesNormalStrings() throws TemplateModelException {
        assertEquals("Fixed Income Operations", nice("FIXED INCOME OPERATIONS"));
    }

    public void testPreservesBFIInCapitals() throws TemplateModelException {
        assertEquals("BFI Fixed Income", nice("BFI FIXED INCOME"));
    }

    public void testIgnoresAcronymsInTheMiddleOfWords() throws TemplateModelException {
        assertEquals("BFI Fixed Income", nice("BFI FIXED INCOME"));
    }

    public void testIgnoresAcronymsAtTheStartOfWords() throws TemplateModelException {
        assertEquals("BFI Fixed Income", nice("BFI FIXED INCOME"));
    }

    public void testFixesAcronymsAtTheEndOfTheString() throws TemplateModelException {
        assertEquals("Fixed Income BFI", nice("FIXED INCOME BFI"));
    }

    public void testIgnoresParenthesesForCapitalization() throws TemplateModelException {
        assertEquals("American Express Bank (Switzerland) SA", nice("AMERICAN EXPRESS BANK (SWITZERLAND) SA"));
    }

    public void testIgnoresSlashesForCapitalization() throws TemplateModelException {
        assertEquals("Banques/Banques Mut. Ou Coop./Caisses De Credit Municipal", nice("BANQUES/BANQUES MUT. OU COOP./CAISSES DE CREDIT MUNICIPAL"));
    }

    private String nice(String up) throws TemplateModelException {
        return (String) method.exec(Lists.build(up));
    }
}
