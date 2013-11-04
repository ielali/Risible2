package risible.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import risible.lucene.events.IndexCreationEvents;

import java.io.IOException;

public class LuceneConsumer implements Runnable, IndexTaskProcessor {
    public static final String ID_FIELD_NAME = "id";

    private final JobQueue jobs;
    private final IndexWriterWrapper writer;
    private final Logger log;
    private String name;
    private boolean dead = false;
    private IndexCreationEvents idxEvents;
    private String indexName;

    public LuceneConsumer(JobQueue jobs, Index index, int i, IndexWriterWrapper writer, IndexCreationEvents idxEvents) throws IOException {
        this.jobs = jobs;
        this.writer = writer;
        this.idxEvents = idxEvents;
        this.indexName = index.getName();
        name = getClass().getName() + "." + index.getName() + "." + i;
        log = Logger.getLogger(name);
    }

    public void run() {
        try {
            while (!dead) {
                try {
                    LuceneDocument doc = (LuceneDocument) jobs.next(this);
                    idxEvents.preConsume(indexName, doc);
                    consume(doc);
                    idxEvents.postConsume(indexName, doc);
                } finally {
                    jobs.idle(this);
                }
            }
        } catch (InterruptedException ex) {
            System.out.println(name + " interrupted: " + ex);
        } finally {
            writer.close();
        }
    }

    private void consume(LuceneDocument document) {
        try {
            updateIndex(document);
        } catch (RuntimeException e) {
            log.error("Error updating document " + document, e);
        } catch (IOException e) {
            log.error("Error updating document " + document, e);
        }
    }

    private void updateIndex(LuceneDocument document) throws IOException {
        Document doc = document.getOriginal();
        Field idField = doc.getField(ID_FIELD_NAME);
        if (idField == null) {
            throw new RuntimeException("No " + ID_FIELD_NAME + " found in document " + document);
        }
        Term term = new Term(ID_FIELD_NAME, idField.stringValue());
        writer.updateDocument(term, doc);
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LuceneConsumer consumer = (LuceneConsumer) o;

        if (!name.equals(consumer.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public void stop() {
        dead = true;
    }
}
