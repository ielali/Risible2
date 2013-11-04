package risible.lucene.events;

import risible.lucene.IndexingJob;
import risible.lucene.LuceneDocument;

public interface IndexCreationEvents {
    void preCreation(String name);

    void postCreation(String name);

    void postOptimization(String name);

    void preOptimization(String name);

    void preConsume(String name, LuceneDocument doc);

    void postConsume(String name, LuceneDocument doc);

    void preProduce(String name, IndexingJob job);

    void postProduce(String name, IndexingJob job);
}
