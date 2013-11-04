package risible.lucene;

import java.io.IOException;

public interface IndexingJob {
    void produceDocuments() throws IOException, InterruptedException;
}
