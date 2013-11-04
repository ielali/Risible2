package risible.lucene;

import org.apache.log4j.Logger;

public class IndexUnlocker {
    private static final Logger log = Logger.getLogger(IndexUnlocker.class);
    private Indices indexGroup;

    public void checkIndexes() throws Exception {
        for (Index index : indexGroup.all()) {
            if (index.isLocked()) {
                log.info("Index " + index + " is locked: will be deleted");
                index.delete();
            }
        }
    }

    public void setIndexGroup(Indices indexGroup) {
        this.indexGroup = indexGroup;
    }
}
