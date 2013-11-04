package risible.lucene;

import java.io.IOException;

public interface HitsProcessor {
    void process(LuceneHits hits) throws IOException;
}
