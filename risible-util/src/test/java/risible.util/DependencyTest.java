package risible.util;

import junit.framework.TestCase;

public class DependencyTest extends TestCase {

    public void testReturnsTrueIfAllDependenciesPresent() {
        Dependency d = new Dependency();
        d.add("foo", "ognl.Ognl");
        assertTrue(d.check());
    }

    public void testThrowsErrorIfAnyDependencyIsMissing() {
        Dependency d = new Dependency();
        d.add("foo", "ognl.Ognl");
        d.add("bar", "toto.googoo.Stuff", "toto.googoo.MoreStuff");
        d.add("yadda", "yadda.flipflop.Stuff", "yadda.flipflop.MoreStuff");
        try {
            d.check();
            fail("No error was thrown!");
        } catch (Dependency.MissingDependency md) {
            assertEquals("Failed to load class toto.googoo.Stuff : toto.googoo.Stuff (expected in library bar)\n" +
                    "Failed to load class toto.googoo.MoreStuff : toto.googoo.MoreStuff (expected in library bar)\n" +
                    "Failed to load class yadda.flipflop.Stuff : yadda.flipflop.Stuff (expected in library yadda)\n" +
                    "Failed to load class yadda.flipflop.MoreStuff : yadda.flipflop.MoreStuff (expected in library yadda)", md.getMessage());
        }
    }
}
