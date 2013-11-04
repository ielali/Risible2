package risible.lucene;

import java.util.Set;

public class IndexStatus {
    private final Object lock;
    private final JobQueue loadJobs;
    private final JobQueue writeJobs;
    private final Set busyObjects;

    public IndexStatus(Object lock, JobQueue loadJobs, JobQueue writeJobs, Set busyObjects) {
        this.lock = lock;
        this.loadJobs = loadJobs;
        this.writeJobs = writeJobs;
        this.busyObjects = busyObjects;
    }

    public int getBusyCount() {
        return busyObjects.size();
    }

    public int getLoadJobCount() {
        return loadJobs.count();
    }

    public int getWriteJobCount() {
        return writeJobs.count();
    }

    public boolean indexing() {
        synchronized (lock) {
            return getBusyCount() > 0 || getLoadJobCount() > 0 || getWriteJobCount() > 0;
        }
    }

    public String toString() {
        return "(" + getLoadJobCount() + ";" + getWriteJobCount() + ";" + getBusyCount() + ")";
    }

    public void waitUntilFinished() throws InterruptedException {
        synchronized (lock) {
            while (indexing()) {
                lock.wait(5000);
            }
        }
    }
}
