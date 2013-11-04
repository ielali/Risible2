/*
 * This file contains code that is Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com, and 
 * released under the terms of the GNU General Public License version 2 or later.
*/

package risible.util;

import java.util.Set;
import java.util.StringTokenizer;

public class Camelise {
    final static String[] map = {
            "!", " bang ",
            "\"", " quote ",
            "#", " hash ",
            "$", " dollar ",
            "%", " percent ",
            "&", " ampersand ",
            "'", " single quote ",
            "(", " left parenthesis ",
            ")", " right parenthesis ",
            "*", " star ",
            "+", " plus ",
            ",", " comma ",
            "-", " minus ",
            ".", " dot ",
            "/", " slash ",
            ":", " colon ",
            ";", " semicolon ",
            "<", " less than ",
            ">", " greater than ",
            "=", " equals ",
            "?", " question ",
            "@", " at ",
            "[", " left square bracket ",
            "]", " right square bracket ",
            "\\", " backslash ",
            "^", " caret ",
            "`", " backquote ",
            "{", " left brace ",
            "}", " right brace ",
            "|", " bar ",
            "~", " tilde "
    };

    private static final Set javaKeywordSet = Lists.buildSet(
            "abstract", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "false", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true", "try",
            "void", "volatile", "while");

    public static String camel(String name) {
        name = name.trim();
        if (name.length() == 0)
            return "blank";
        for (int i = 0; i < map.length; i += 2) {
            String from = map[i];
            String to = map[i + 1];
            while (true) {
                int pos = name.indexOf(from);
                if (pos < 0)
                    break;
                name = name.substring(0, pos) + to + name.substring(pos + from.length());
            }
        }
        if (Character.isDigit(name.charAt(0)))
            name = mapNumber(name.charAt(0)) + name.substring(1);
        if (Character.isUpperCase(name.charAt(0)))
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        return hideJavaKeyword(translateUnicode(camelise(name)));
    }

    private static String camelise(String name) {
        StringTokenizer tokenizer = new StringTokenizer(name);
        String result = tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            result += token.substring(0, 1).toUpperCase() + token.substring(1);
        }
        return result;
    }

    private static String hideJavaKeyword(String name) {
        if (javaKeywordSet.contains(name))
            return name + "_";
        return name;
    }

    private static String translateUnicode(String name) {
        StringBuffer b = new StringBuffer(name.length());
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i); // This needs to be updated for jdk1.5 to codePointAt()
            if (ch < 128) // not unicode
                b.append(ch);
            else
                b.append("u" + Integer.toHexString(ch).toUpperCase());
        }
        name = b.toString();
        return name;
    }

    private static String mapNumber(char c) {
        switch (c) {
            case '0':
                return "zero ";
            case '1':
                return "one ";
            case '2':
                return "two ";
            case '3':
                return "three ";
            case '4':
                return "four ";
            case '5':
                return "five ";
            case '6':
                return "six ";
            case '7':
                return "seven ";
            case '8':
                return "eight ";
            case '9':
                return "nine ";
        }
        return "" + c;
    }
}
