package risible.lucene;

import java.io.IOException;
import java.util.List;

public class SimpleIndexingJob implements IndexingJob {
    private final Class clazz;
    private final List ids;
    private final ObjectLoader repository;
    private final JobQueue writeJobs;

    public SimpleIndexingJob(Class clazz, List ids, ObjectLoader repository, JobQueue writeJobs) {
        this.clazz = clazz;
        this.ids = ids;
        this.repository = repository;
        this.writeJobs = writeJobs;
    }

    public void produceDocuments() throws IOException {
        for (Object o : findObjects()) {
            Indexable dom = (Indexable) o;
            try {
                writeJobs.put(dom.populate(new LuceneDocument()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        repository.clearSession();
    }

    public String toString() {
        if (ids.size() < 10) {
            return getClass().getSimpleName() + "/" + clazz.getSimpleName() + "(" + clazz.getSimpleName() + ")" + ids;
        } else {
            Object first = ids.get(0);
            Object last = ids.get(ids.size() - 1);
            return getClass().getSimpleName() + "/" + clazz.getSimpleName() + "/" + ids.size() + "(" + first + "-" + last + ")";
        }
    }

    public List findObjects() {
        return repository.findObjects(clazz, ids);
    }
}
