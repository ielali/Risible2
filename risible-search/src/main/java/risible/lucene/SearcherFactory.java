package risible.lucene;

import org.apache.lucene.search.*;
import risible.util.Remember;

import java.io.IOException;

public class SearcherFactory {
    private Remember remember;
    private IndexAccessController accessController;

    public void search(Index index, Query query, Sort sort, HitsProcessor hitsProcessor) throws IOException {
        query = accessController.constrain(index, query);
        Searcher searcher = createSearcher(index);
        remember.todo(close(searcher));
        try {
            hitsProcessor.process(new LuceneHits(searcher.search(query, sort)));
        } catch (BooleanQuery.TooManyClauses e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to run query " + query + " (sort by " + sort + ") on index " + index, e);
        } catch (IOException e) {
            throw (IOException) new IOException("Failed to run query " + query + " (sort by " + sort + ") on index " + index).initCause(e);
        }
    }

    protected Searcher createSearcher(Index index) throws IOException {
        return new IndexSearcher(index.getIndexDirectory());
    }

    private Runnable close(final Searcher searcher) {
        return new Runnable() {
            public void run() {
                try {
                    searcher.close();
                } catch (IOException e) {
                    throw new RuntimeException("Closing searcher " + searcher, e);
                }
            }
        };
    }

    public void setRemember(Remember remember) {
        this.remember = remember;
    }

    public void setAccessController(IndexAccessController accessController) {
        this.accessController = accessController;
    }
}
