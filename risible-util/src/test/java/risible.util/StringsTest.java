package risible.util;

import junit.framework.TestCase;

public class StringsTest extends TestCase {

    public void testIsYReturnsTrueWhenYIsGiven() {
        assertTrue(Strings.isY("Y"));
    }

    public void testIsYReturnsFalseWhenNIsGiven() {
        assertFalse(Strings.isY("N"));
    }

    public void testIsYReturnsFalseWhenNullIsGiven() {
        assertFalse(Strings.isY(null));
    }

    public void testProperlyCapitalizeStrings() {
        assertEquals("Nick O'Brien-Wamsteker J.Unit", Strings.nice("NICK O'BRIEN-WAMSTEKER J.UNIT"));
        assertEquals("Nick O'Brien-Wamsteker J.Unit", Strings.nice("nick o'brien-wamsteker j.unit"));
    }

    public void testRemovesAllWhitespaces() {
        assertEquals("toto", Strings.removeAllWhiteSpaces("  \r\nto  \r\nto  \r\n"));
    }

    public void testReturnsAnEmptyStringIfRemovesAllWhitespacesOfAStringContainingOnlyWhitespaces() {
        assertEquals("", Strings.removeAllWhiteSpaces("  \r\n  \r\n  \r\n"));
    }

    public void testReturnsNullIfRemovingAllWhitespacesOfANullString() {
        assertNull(Strings.removeAllWhiteSpaces(null));
    }

    public void testPairsParams() {
        Pair[] actual = Strings.parseQueryString("a=b&c=&a=&e=h&x=&y=&z=p&x=");
        Pair[] expected = {
                new Pair("a", "b"),
                new Pair("e", "h"),
                new Pair("z", "p"),
        };

        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].name, actual[i].name);
            assertEquals(expected[i].value, actual[i].value);
        }
    }

}
