package risible.lucene;

import java.io.IOException;

public class DocumentHitProxy {
    private LuceneHits hits;
    private int index;

    public DocumentHitProxy(LuceneHits hits, int index) {
        this.hits = hits;
        this.index = index;
    }

    public LuceneDocument getDocument() throws IOException {
        return hits.doc(index);
    }

    public Object get(String field) throws IOException {
        return getDocument().get(field);
    }
}
