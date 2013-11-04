package risible.lucene;

import java.io.IOException;

public class SingleIndexingJob implements IndexingJob {
    private final Object object;
    private final Jobs writeJobs;

    public SingleIndexingJob(Object object, Jobs writeJobs) {
        this.object = object;
        this.writeJobs = writeJobs;
    }

    public void produceDocuments() throws IOException {
        Indexable dom = (Indexable) object;
        try {
            writeJobs.put(dom.populate(new LuceneDocument()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "/" + object;
    }
}
