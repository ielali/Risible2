package risible.util;

import junit.framework.TestCase;

public class TextHelperTest extends TestCase {

    public void testExtractsKeyFromBetweenSquareBrackets() {
        String text = "[123] foo bar";
        String expected = "123";
        assertEquals(expected, TextHelper.extractKey(text));
    }

    public void testMakesNoChangeWhenThereAreNoSquareBrackets() {
        String text = "foo bar";
        assertEquals(text, TextHelper.extractKey(text));
    }

    public void testExtractKeyHandlesNull() {
        assertEquals(null, TextHelper.extractKey(null));
    }

    public void testExtractKeyHandlesEmptyString() {
        assertEquals("", TextHelper.extractKey(""));
    }
}
