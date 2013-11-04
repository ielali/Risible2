package risible.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.hibernate.Session;
import risible.hibernate.Operation;
import risible.hibernate.SessionOperationRunner;

public class IndexingService {
    private static final Logger log = Logger.getLogger(IndexingService.class);
    public static final Analyzer ANALYZER = new SimplerAnalyzer();

    private SessionOperationRunner hibernate;
    private Indices indices;
    private boolean allowReindexEverything;
    private boolean allowIncrementalIndexing;

    public void reindex(final Index[] indexes) throws Exception {
        hibernate.inSession(true, new Operation() {
            public Object run(Session s) throws InterruptedException {
                for (Index index : indexes) {
                    index.reindex();
                    index.getStatus().waitUntilFinished();
                }
                return null;
            }
        });
    }

    public void reindexEverything() throws Exception {
        if (allowReindexEverything) {
            reindex(indices.all());
        }
    }

    public void incrementalIndexing() throws Exception {
        if (!allowIncrementalIndexing) {
            return;
        }

        hibernate.inSession(true, new Operation() {
            public Object run(Session s) throws InterruptedException {
                for (Index index : indices.all()) {
                    index.indexIncremental();
                    index.getStatus().waitUntilFinished();
                }
                return null;
            }
        });
    }

    public void setHibernate(SessionOperationRunner hibernate) {
        this.hibernate = hibernate;
    }

    public void setIndexGroup(Indices indices) {
        this.indices = indices;
    }

    public void setAllowReindexEverything(boolean allowReindexEverything) {
        this.allowReindexEverything = allowReindexEverything;
    }

    public boolean allowsReindexEverything() {
        return allowReindexEverything;
    }

    public boolean allowsIncrementalIndexing() {
        return allowIncrementalIndexing;
    }

    public void disableIncrementalIndexing() {
        this.allowIncrementalIndexing = false;
    }

    public void enableIncrementalIndexing() {
        this.allowIncrementalIndexing = true;
    }
}
