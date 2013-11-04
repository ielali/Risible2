package risible.util;

import org.apache.commons.lang.StringUtils;

public class TextHelper {
    public static String extendedCamel(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        } else {
            return Camelise.camel(text.toLowerCase());
        }
    }

    public static String extractKey(String text) {
        return text == null ? null : text.replaceAll("\\[([a-zA-Z0-9]+)\\].*", "$1");
    }
}
