package risible.lucene;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import risible.hibernate.Operation;
import risible.hibernate.SessionOperationRunner;
import risible.lucene.events.IndexCreationEvents;

import java.io.IOException;

public class LuceneProducer implements Runnable, IndexTaskProcessor {
    private Logger log;

    private final Index index;
    private final SessionOperationRunner hibernate;
    private final JobQueue jobs;
    private String name;
    private boolean dead = false;
    private IndexCreationEvents idxEvents;

    public LuceneProducer(JobQueue jobs, int i, Index index, SessionOperationRunner hibernate, IndexCreationEvents idxEvents) {
        this.jobs = jobs;
        this.index = index;
        this.hibernate = hibernate;
        this.name = getClass().getName() + "." + index.getName() + "." + i;
        this.idxEvents = idxEvents;
        log = Logger.getLogger(this.name);
    }

    public void run() {
        try {
            while (!dead) {
                try {
                    IndexingJob job = (IndexingJob) jobs.next(this);
                    long now = System.currentTimeMillis();
                    idxEvents.preProduce(index.getName(), job);
                    produceDocuments(job);
                    idxEvents.postProduce(index.getName(), job);
                    log.info(job + " took " + (System.currentTimeMillis() - now) + "ms," + index.getStatus());
                } finally {
                    jobs.idle(this);
                }
            }
        } catch (InterruptedException ex) {
            System.out.println(name + " interrupted: " + ex);
        }
    }

    private void retry(IndexingJob job) throws InterruptedException {
        jobs.put(job);
    }

    private void produceDocuments(final IndexingJob job) throws InterruptedException {
        try {
            hibernate.inSession(true, new Operation() {
                public Object run(Session s) throws IOException, InterruptedException {
                    job.produceDocuments();
                    return null;
                }
            });
        } catch (NullPointerException e) {
            log.warn("Indexer Thread problem", e);
            retry(job);
        } catch (GenericJDBCException e) {
            log.warn("Indexer Thread problem", e);
            retry(job);
        } catch (Throwable t) {
            log.error("Failed to complete " + job + "; caught " + t.getClass().getName(), t);
        }
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

        LuceneProducer producer = (LuceneProducer) o;

        if (!name.equals(producer.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public void stop() {
        dead = true; // todo: this isn't enough - what if we're in an Object.wait() ?
    }
}
