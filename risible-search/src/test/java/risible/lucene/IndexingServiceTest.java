package risible.lucene;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import risible.hibernate.Hibernate;
import risible.hibernate.Operation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IndexingServiceTest {
    private Hibernate hibernate;
    private Indices indices;
    private IndexingService service;
    private List indexLog = new ArrayList();

    @Before
    public void setUp() {
        hibernate = new Hibernate() {

            @Override
            public Object inSession(boolean readOnly, Operation o) throws Exception {
                o.run(null);
                return null;
            }
        };
        indices = Mockito.mock(Indices.class);
        Mockito.when(indices.all()).thenReturn(new Index[]{createIndex("foo"), createIndex("bar")});
        service = new IndexingService();
        service.setHibernate(hibernate);
        service.setIndexGroup(indices);
    }

    @Test
    public void testRunsIncrementalIndexOnAllIndexes() throws Exception {
        service.enableIncrementalIndexing();
        service.incrementalIndexing();

        assertEquals("[incremental indexed foo, incremental indexed bar]", indexLog.toString());
    }


    @Test
    public void testRunsFullReindexOnAllIndexes() throws Exception {
        service.setAllowReindexEverything(true);
        service.reindexEverything();

        assertEquals("[reindexed foo, reindexed bar]", indexLog.toString());
    }

    private Index createIndex(final String name) {
        Index a = new Index(createIndexSpec(name)) {
            public void reindex() throws InterruptedException {
                indexLog.add("reindexed " + name);
            }

            public void indexIncremental() throws InterruptedException {
                indexLog.add("incremental indexed " + name);
            }
        };
        return a;
    }

    private IndexSpec createIndexSpec(String name) {
        IndexSpec spec = new IndexSpec();
        spec.name = name;
        spec.loadJobs = new Jobs(spec.lock, 10, spec.busyObjects);
        spec.writeJobs = new Jobs(spec.lock, 10, spec.busyObjects);
        spec.buildStatus();
        return spec;
    }

    static class Foo {
    }

    static class Bar {
    }

}
