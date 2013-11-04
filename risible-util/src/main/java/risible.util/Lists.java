package risible.util;

import ognl.Ognl;

import java.util.*;

public class Lists implements RisibleUtil {
    public static List<List> splitIntoBatches(List original, int batchSize) {
        List<List> result = new LinkedList<List>();
        for (int start = 0, end = batchSize; start < original.size(); start = end, end += batchSize) {
            if (end > original.size()) {
                end = original.size();
            }
            result.add(original.subList(start, end));
        }
        return result;
    }

    public static List<List> split(List original, int columns) {
        List<List> result = new ArrayList(columns);
        for (int i = 0; i < columns; i++) {
            result.add(new LinkedList());
        }

        if (original == null) {
            return result;
        }

        int itemsPerColumn = original.size() / columns;
        if (itemsPerColumn * columns < original.size()) {
            itemsPerColumn++;
        }

        for (int i = 0; i < original.size(); i++) {
            List column = result.get(i / itemsPerColumn);
            column.add(original.get(i));
        }

        return result;
    }

    public static String join(Iterable things, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Object thing : things) {
            String value = thing != null ? thing.toString() : "";
            if (value.length() > 0) {
                sb.append(thing).append(separator);
            }
        }
        String result = sb.toString();
        if (result.length() > 0) {
            result = result.substring(0, result.length() - separator.length());
        }
        return result;
    }

    public static <T> List<T> build(T... o) {
        if (o == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<T>(Arrays.asList(o));
    }

    public static <T> Set<T> buildSet(T... o) {
        if (o == null) {
            return Collections.EMPTY_SET;
        }
        return new HashSet<T>(Arrays.asList(o));
    }

    public static char[] segment(char[] cbuf, int offset, int length) {
        char[] result = new char[length];
        System.arraycopy(cbuf, offset, result, 0, length);
        return result;
    }

    public static <T> T findFirst(Collection<T> things, String booleanProperty) {
        Collection<T> all = findAll(things, booleanProperty);
        return all.isEmpty() ? null : all.iterator().next();
    }

    public static <T> Collection<T> findAll(Collection<T> things, String booleanProperty) {
        LinkedList<T> list = new LinkedList<T>();
        for (T thing : things) {
            try {
                if ((Boolean) Ognl.getValue(booleanProperty, thing)) {
                    list.add(thing);
                }
            } catch (Exception e) {
                throw new Error("unable to get property " + booleanProperty + " of " + thing, e);
            }
        }
        return list;
    }

    public static <T> boolean contains(T object, T... objects) {
        for (T o : objects) {
            if (o.equals(object)) {
                return true;
            }
        }
        return false;
    }
}
