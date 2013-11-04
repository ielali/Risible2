package risible.lucene;

import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.util.Date;

public class IndexInfo implements Comparable<IndexInfo> {
    private String name;
    private boolean optimized;
    private int count;
    private boolean exists;
    private boolean locked;
    private Date lastModified;
    private IndexStatus status;

    public IndexInfo(Index index) throws IOException {
        this.name = index.getName();
        this.status = index.getStatus();
        this.exists = IndexReader.indexExists(index.getIndexDirectory());

        if (this.exists) {
            IndexReader r = null;
            try {
                r = IndexReader.open(index.getIndexDirectory());
                this.optimized = r.isOptimized();
                this.count = r.numDocs();
            } finally {
                if (r != null) {
                    r.close();
                }
            }

            this.locked = IndexReader.isLocked(index.getIndexDirectory());
            this.lastModified = new Date(IndexReader.lastModified(index.getIndexDirectory()));
        }
    }

    public String getName() {
        return name;
    }

    public IndexStatus getStatus() {
        return status;
    }

    public boolean isOptimized() {
        return optimized;
    }

    public int getCount() {
        return count;
    }

    public boolean isExists() {
        return exists;
    }

    public boolean isLocked() {
        return locked;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public int compareTo(IndexInfo o) {
        return name.compareTo(o.name);
    }
}
