package risible.lucene;

import java.util.HashSet;
import java.util.Set;

public class IndexSpec {
    public Object lock = new Object();
    public Set busyObjects = new HashSet();
    public ObjectLoader repository;
    public String name;
    public Class[] classes;
    public int batchSize;
    public boolean incremental;
    public String path;
    public JobQueue loadJobs;
    public JobQueue writeJobs;
    public IndexStatus status;

    public void buildStatus() {
        this.status = new IndexStatus(lock, loadJobs, writeJobs, busyObjects);
    }
}
