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

public class HighlightMethodTest extends TestCase {

    public void testHighlightsPrefixTerms() throws TemplateModelException {
        String expectedText = "hello <span class=\"highlight\">barclays</span> bank <span class=\"highlight\">world</span>, " +
                "this is \"some <span class=\"highlight\">embarcingly</span> <span class=\"highlight\">worrying</span> text\"!";

        String input = "hello barclays bank world, this is \"some embarcingly worrying text\"!";
        assertEquals(expectedText, decorate(input, "barc wor"));
    }

    public void testHighlightsTermsRegardlessOfCase() throws TemplateModelException {
        String expectedText = "HELLO <span class=\"highlight\">BARCLAYS</span> BANK <span class=\"highlight\">WORLD</span>, " +
                "THIS IS \"SOME <span class=\"highlight\">EMBARCINGLY</span> <span class=\"highlight\">WORRYING</span> TEXT\"!";

        String input = "HELLO BARCLAYS BANK WORLD, THIS IS \"SOME EMBARCINGLY WORRYING TEXT\"!";
        assertEquals(expectedText, decorate(input, "barc wor"));
    }

    public void testIncludesInfixOccurrencesOfTerm() throws TemplateModelException {
        String expectedText = "hello <span class=\"highlight\">blahbarclays</span> <span class=\"highlight\">blahworld</span>";
        String input = "hello blahbarclays blahworld";
        assertEquals(expectedText, decorate(input, "barc wor"));
    }

    public void testFindsTermsAtBeginningAndEndOfText() throws TemplateModelException {
        String expectedText = "<span class=\"highlight\">barclays</span> bank barks in <span class=\"highlight\">london</span>";
        String input = "barclays bank barks in london";
        assertEquals(expectedText, decorate(input, "barc lon"));
    }

    public void testConsidersWholeNumbersOnly() throws TemplateModelException {
        String expectedText = "<span class=\"highlight\">28</span> <span class=\"highlight\">3130</span> 1986 3230";
        assertEquals(expectedText, decorate("28 3130 1986 3230", "3130 28 32 86"));
    }

    public void testDoesNotHighlightCodeUsedForHighlighting() throws TemplateModelException {
        String expectedText = "<span class=\"highlight\">barc</span> <span class=\"highlight\">cl</span> your hands";
        assertEquals(expectedText, decorate("barc cl your hands", "barc cl"));
    }

    public void testDoesNotHighlightTheSameTermSeveralTimes() throws TemplateModelException {
        String expectedText = "<span class=\"highlight\">clapping</span> your hands";
        assertEquals(expectedText, decorate("clapping your hands", "clap ping app"));
    }

    public void testHighlightsMultipleAdjacentMatches() throws TemplateModelException {
        String expectedText = "<span class=\"highlight\">BANCLAYS</span> <span class=\"highlight\">BANK</span> PLC";
        assertEquals(expectedText, decorate("BANCLAYS BANK PLC", "ban"));
    }

    public void testDoesNotHighlightLessThan3CharactersExpression() throws TemplateModelException {
        String expectedText = "BARCLAYS BANK PLC";
        assertEquals(expectedText, decorate("BARCLAYS BANK PLC", "ba"));
    }

    public void testHighlightsWholeWordIfLessThan3CharactersExpression() throws TemplateModelException {
        String expectedText = "BARCLAYS BANK <span class=\"highlight\">ba</span> PLC";
        assertEquals(expectedText, decorate("BARCLAYS BANK ba PLC", "ba"));
    }

    public void testDoesNotAttemptToHighlightWithinAnchorTag() throws TemplateModelException {
        String expectedText = "<a href='foobar'><span class=\"highlight\">BARCLAYS</span> BANK PLC</a>";
        assertEquals(expectedText, decorate("<a href='foobar'>BARCLAYS BANK PLC</a>", "bar"));
    }

    public void testDoesNotAttemptToHighlightWithinAnchorTagEspeciallyWithSmallSearchTerms() throws TemplateModelException {
        String expectedText = "<a href='foobar'>BARCLAYS BANK PLC</a>";
        assertEquals(expectedText, decorate("<a href='foobar'>BARCLAYS BANK PLC</a>", "a c"));
    }

    public void testDoesNotAttemptToHighlightWithinImgTag() throws TemplateModelException {
        String expectedText = "<img src='foobar' alt='barfoo'/><span class=\"highlight\">BARCLAYS</span> BANK PLC";
        assertEquals(expectedText, decorate("<img src='foobar' alt='barfoo'/>BARCLAYS BANK PLC", "bar"));
    }

    public void testHighlightsSingleDigitResults() throws TemplateModelException {
        String expectedText = "<img src='foobar' alt='barfoo'/><span class=\"highlight\">1</span> BANK PLC";
        assertEquals(expectedText, decorate("<img src='foobar' alt='barfoo'/>1 BANK PLC", "1"));
    }

    public void testNoHighlightWhenSearchIsEmpty() throws TemplateModelException {
        String expectedText = "BARCLAYS BANK PLC";
        assertEquals(expectedText, decorate("BARCLAYS BANK PLC", ""));
    }

    public void testNoHighlightWhenSearchIsNull() throws TemplateModelException {
        String expectedText = "BARCLAYS BANK PLC";
        assertEquals(expectedText, decorate("BARCLAYS BANK PLC", null));
    }

    public void testPreservesDollarsWithinTags() throws TemplateModelException {
        String expectedText = "<img src='foobar' onclick=\"$('foobar').hide()\"/><span class=\"highlight\">yadda</span> bank plc";
        assertEquals(expectedText, decorate("<img src='foobar' onclick=\"$('foobar').hide()\"/>yadda bank plc", "yad"));
    }

    private Object decorate(String actualText, final String searchExpression) throws TemplateModelException {
        HighlightMethod m = new HighlightMethod() {
            protected String getSearchExpression() {
                return searchExpression;
            }
        };

        return m.exec(Lists.build(actualText));
    }
}
