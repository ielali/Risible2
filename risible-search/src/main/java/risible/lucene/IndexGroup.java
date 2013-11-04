package risible.lucene;

import java.util.List;

public class IndexGroup implements Indices {
    private final List<Index> indices;

    public IndexGroup(List<Index> indices) {
        this.indices = indices;
    }

    public Index getIndex(String name) {
        for (Index index : indices) {
            if (index.getName().equalsIgnoreCase(name)) {
                return index;
            }
        }
        throw new IllegalArgumentException("No index found for: " + name);
    }

    public Index[] all() {
        return indices.toArray(new Index[indices.size()]);
    }

    public void cancelAll() {
        for (Index index : indices) {
            index.cancelAllJobs();
        }
    }
}
