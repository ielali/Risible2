package risible.lucene;

import org.apache.lucene.search.Hits;

import java.io.IOException;

public class LuceneHits {
    private Hits hits;

    public LuceneHits(Hits hits) {
        this.hits = hits;
    }

    public int length() {
        return hits.length();
    }

    public LuceneDocument doc(int index) throws IOException {
        return new LuceneDocument(hits.doc(index));
    }
}
