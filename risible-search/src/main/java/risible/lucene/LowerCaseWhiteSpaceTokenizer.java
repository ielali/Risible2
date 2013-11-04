package risible.lucene;

import org.apache.lucene.analysis.CharTokenizer;

import java.io.Reader;

public class LowerCaseWhiteSpaceTokenizer extends CharTokenizer {
    public LowerCaseWhiteSpaceTokenizer(Reader input) {
        super(input);
    }

    protected boolean isTokenChar(char c) {
        return !Character.isWhitespace(c);
    }

    protected char normalize(char c) {
        return Character.toLowerCase(c);
    }
}
