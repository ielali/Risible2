package risible.lucene.events;

import risible.lucene.IndexingJob;
import risible.lucene.LuceneDocument;

public class NoIndexCreationEvents implements IndexCreationEvents {
    public void preCreation(String name) {
    }

    public void postCreation(String name) {
    }

    public void preOptimization(String name) {
    }

    public void postOptimization(String name) {
    }

    public void preConsume(String name, LuceneDocument doc) {
    }

    public void postConsume(String name, LuceneDocument doc) {
    }

    public void preProduce(String name, IndexingJob job) {
    }

    public void postProduce(String name, IndexingJob job) {
    }
}
