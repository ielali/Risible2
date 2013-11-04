package risible.lucene;

import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import risible.util.Files;
import risible.util.Lists;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Index {
    private static final int ONE_DAY = 1000 * 60 * 60 * 24;

    private final Logger log;
    private final Object lock;
    private final ObjectLoader repository;
    private final String name;
    private final Class[] classes;
    private final int batchSize;
    private final boolean incremental;
    private final String path;
    private final JobQueue loadJobs;
    private final JobQueue writeJobs;
    private final IndexStatus status;

    private Date lastIncrementalIndex;

    public Index(IndexSpec spec) {
        this.lock = spec.lock;
        this.repository = spec.repository;
        this.name = spec.name;
        this.classes = spec.classes;
        this.batchSize = spec.batchSize;
        this.incremental = spec.incremental;
        this.path = spec.path;
        this.loadJobs = spec.loadJobs;
        this.writeJobs = spec.writeJobs;
        this.status = spec.status;
        log = Logger.getLogger(Index.class.getName() + "." + name);
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public void reindex() throws InterruptedException {
        if (status.indexing()) {
            return;
        }
        log.debug("reindexing " + getName());
        index(findAllIds());
    }

    public void indexIncremental() throws InterruptedException {
        if (status.indexing() || !incremental) {
            return;
        }
        index(findAllIncrementalIds());
    }

    public void index(Map<Class, List> stuff) throws InterruptedException {
        for (Map.Entry<Class, List> entry : stuff.entrySet()) {
            List ids = entry.getValue();
            List<List> batches = Lists.splitIntoBatches(ids, batchSize);
            if (ids.size() > 0) {
                log.info("Indexing " + ids.size() + " of " + entry.getKey().getSimpleName() + " in " + batches.size() + " batches for " + getName() + " index");
            }
            for (List batch : batches) {
                loadJobs.put(new SimpleIndexingJob(entry.getKey(), batch, repository, writeJobs));
            }
        }
    }

    public void indexUrgently(LuceneDocument doc) throws InterruptedException {
        writeJobs.put(doc);
    }

    public void cancelAllJobs() {
        synchronized (lock) {
            loadJobs.cancel();
            writeJobs.cancel();
        }
    }

    private Map<Class, List> findAllIds() {
        Map<Class, List> result = new LinkedHashMap<Class, List>();
        for (Class clazz : classes) {
            result.put(clazz, repository.findAllIds(clazz));
        }
        return result;
    }

    private Map<Class, List> findAllIncrementalIds() {
        Date now = new Date();
        Map<Class, List> result = new LinkedHashMap<Class, List>();

        if (lastIncrementalIndex == null) {
            lastIncrementalIndex = new Date(System.currentTimeMillis() - ONE_DAY);
        }

        for (Class clazz : classes) {
            result.put(clazz, repository.findUpdatedIds(clazz, lastIncrementalIndex));
        }

        lastIncrementalIndex = now;
        return result;
    }

    public void delete() {
        Files.delete(new File(path));
    }

    public Directory getIndexDirectory() throws IOException {
        return FSDirectory.getDirectory(Files.ensureDirectory(path));
    }

    public boolean isLocked() {
        File indexDirectory = Files.ensureDirectory(path);
        File[] locks = indexDirectory.listFiles((FilenameFilter) new NameFileFilter("write.lock"));
        return locks.length > 0;
    }

    public IndexStatus getStatus() {
        return status;
    }
}
