package risible.util;

import java.util.HashMap;
import java.util.Map;

public class Maps implements RisibleUtil {
    public static Map make(Object... keyValuePairs) {
        Map map = new HashMap();
        assert keyValuePairs.length % 2 == 0;
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return map;
    }

    public static String stringify(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(writeParams(entry.getKey(), entry.getValue()));
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String writeParams(String key, Object list) {
        StringBuilder sb = new StringBuilder();
        if (list instanceof String[]) {
            writeStringArray(list, sb, key);
        } else if (list instanceof Iterable) {
            writeIterable(list, sb, key);
        }
        return sb.toString();
    }

    private static void writeIterable(Object list, StringBuilder sb, String key) {
        Iterable<String> values = (Iterable) list;
        for (String value : values) {
            sb
                    .append(key)
                    .append("=")
                    .append(value)
                    .append("&");
        }
    }

    private static void writeStringArray(Object list, StringBuilder sb, String key) {
        String[] values = (String[]) list;
        for (String value : values) {
            sb
                    .append(key)
                    .append("=")
                    .append(value)
                    .append("&");
        }
    }
}
