package risible.util;

public interface RisibleUtil {
    boolean risible_util_dependencies_ok = new RisibleUtilDependency().check();

    class RisibleUtilDependency extends Dependency {
        {
            add("commons-lang-2.3",
                    "org.apache.commons.lang.StringEscapeUtils",
                    "org.apache.commons.lang.StringUtils",
                    "org.apache.commons.lang.WordUtils");
            add("ognl-2.6.7",
                    "ognl.Ognl");
        }
    }
}
