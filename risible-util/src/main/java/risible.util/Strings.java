package risible.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Strings implements RisibleUtil {
    private static final char[] CAPITALISATION_DELIMITERS = " .-'".toCharArray();

    public static Boolean nullableBoolean(String yOrN) {
        if (yOrN == null) {
            return null;
        } else {
            return isY(yOrN);
        }
    }

    public static boolean isY(String value) {
        return "Y".equalsIgnoreCase(value);
    }

    public static boolean isYes(String value) {
        return "yes".equalsIgnoreCase(value);
    }

    public static String toYorN(Boolean b) {
        return (b != null && b) ? "Y" : "N";
    }

    public static void append(StringBuffer sb, String content, String separator) {
        if (StringUtils.isBlank(content)) {
            return;
        }

        if (sb.length() > 0) {
            sb.append(separator);
        }

        sb.append(content);
    }

    public static String toYesorNo(boolean b) {
        return b ? "Yes" : "No";
    }

    public static String xenc(Object something) {
        if (something == null) {
            return "";
        }
        return StringEscapeUtils.escapeHtml(something.toString());
    }

    public static String[] xenc(Object[] somethings) {
        String[] result = new String[somethings.length];
        for (int i = 0; i < somethings.length; i++) {
            result[i] = xenc(somethings[i]);
        }
        return result;
    }

    public static String nice(String text) {
        if (text == null) {
            return null;
        }
        return WordUtils.capitalizeFully(text.toLowerCase(), CAPITALISATION_DELIMITERS);
    }

    public static String valueOf(Object o) {
        return valueOf(o, "");
    }

    public static String valueOf(Object o, String valueIfNull) {
        return o == null ? valueIfNull : o.toString();
    }

    public static String removeAllWhiteSpaces(String text) {
        if (text == null) {
            return null;
        }

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isWhitespace(ch)) {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String stripNonWords(String searchExpression) {
        return searchExpression.replaceAll("\\W", " ").trim().replaceAll("\\s+", " ");
    }

    public static Pair[] parseQueryString(String params) {
        String[] strings = params.split("&");
        List<Pair> result = new LinkedList<Pair>();
        for (String s : strings) {
            String[] keyValue = s.split("=");
            if (keyValue.length > 1) {
                result.add(new Pair(keyValue[0], keyValue[1]));
            }
        }
        return result.toArray(new Pair[result.size()]);
    }

    private static final String characters = "abcdefghjklmnpqrstuvwxyz-23456789+@";
    private static final Random random = new Random();

    public static String random(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}
