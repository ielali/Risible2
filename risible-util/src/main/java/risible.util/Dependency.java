package risible.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Dependency {
    List errors = new LinkedList();

    protected void add(String library, String... classNames) {
        for (String className : classNames) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                errors.add("Failed to load class " + className + " : " + e.getMessage() + " (expected in library " + library + ")");
            }
        }
    }

    public boolean check() {
        if (!errors.isEmpty()) {
            throw new MissingDependency(errors);
        }
        return true;
    }

    public static class MissingDependency extends Error {
        public MissingDependency(Collection errors) {
            super(Lists.join(errors, "\n"));
        }
    }
}
