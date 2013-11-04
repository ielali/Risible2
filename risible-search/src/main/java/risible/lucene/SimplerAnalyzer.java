package risible.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.Reader;

public class SimplerAnalyzer extends Analyzer {
    public TokenStream tokenStream(String fieldName, Reader reader) {
        if ("content".equals(fieldName)) {
            return new LowerCaseWhiteSpaceTokenizer(reader);
        } else if (fieldName.toLowerCase().endsWith("id")) {
            return new KeywordTokenizer(reader);
        } else {
            return new StandardAnalyzer(new String[0]).tokenStream(fieldName, reader);
        }
    }
}
