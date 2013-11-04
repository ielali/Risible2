

package risible.lucene;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import risible.hibernate.SessionOperationRunner;
import risible.lucene.events.IndexCreationEvents;
import risible.lucene.events.NoIndexCreationEvents;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Conan Dalton
 * @author Nazir Khan
 * @author Jean-Philippe Hallot
 */
public class IndexGroupFactoryBean implements InitializingBean, FactoryBean {
    private static final Logger log = Logger.getLogger(IndexGroupFactoryBean.class);
    private static final String INCREMENTAL_OPTION = "incremental";
    private static final String CLASSES = "classes";
    private static final String LOADER_THREADS = "loaderThreads";
    private static final String WRITER_THREADS = "writerThreads";

    private ObjectLoader repository;
    private SessionOperationRunner hibernate;
    private String indexRootDirectory;
    private int batchSize;
    private List<String> configs;
    private int writeJobLimit;
    private IndexGroup indexGroup;
    private Collection<IndexTaskProcessor> taskProcessors = new ArrayList();
    private ThreadGroup taskThreads = new ThreadGroup("Indexing Threads");
    private IndexCreationEvents indexCreationEvents = new NoIndexCreationEvents();

    public void setConfigs(List configs) {
        this.configs = configs;
    }

    public void setRepository(ObjectLoader repository) {
        this.repository = repository;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void afterPropertiesSet() throws Exception {
        List indices = new LinkedList();
        for (String config : configs) {
            Properties props = new Properties();
            props.load(new ByteArrayInputStream(config.getBytes()));

            indices.add(createIndex(props));
        }
        this.indexGroup = new IndexGroup(indices);
    }

    private int intProperty(Properties props, String prop) {
        return Integer.parseInt(props.getProperty(prop));
    }

    private Index createIndex(Properties props) throws ClassNotFoundException, IOException {
        validateIndexConfigProperties(props);
        IndexSpec spec = createSpec(props);
        Index index = new Index(spec);
        startThreads(index, spec, intProperty(props, LOADER_THREADS), intProperty(props, WRITER_THREADS));
        return index;
    }

    private void validateIndexConfigProperties(Properties props) {
        for (Object key : props.keySet()) {
            if (!key.equals(LOADER_THREADS) && !key.equals(WRITER_THREADS) && !key.equals(INCREMENTAL_OPTION) && !key.equals("name") && !key.equals(CLASSES))
                throw new RuntimeException("Unknown property for the index found. Property name: " + key);
        }
    }

    private IndexSpec createSpec(Properties props) throws ClassNotFoundException {
        IndexSpec spec = new IndexSpec();
        spec.loadJobs = new Jobs(spec.lock, Integer.MAX_VALUE, spec.busyObjects);
        spec.writeJobs = new Jobs(spec.lock, writeJobLimit, spec.busyObjects);
        spec.repository = repository;
        spec.batchSize = batchSize;
        String indexName = props.getProperty("name");
        spec.name = indexName;
        spec.path = indexRootDirectory + "/" + indexName;
        spec.classes = loadClasses(props.getProperty(CLASSES));
        spec.incremental = "true".equalsIgnoreCase(props.getProperty(INCREMENTAL_OPTION));
        spec.buildStatus();
        return spec;
    }

    private void startThreads(Index index, IndexSpec spec, int loaderThreads, int writerThreads) throws IOException {
        IndexWriterWrapper writerWrapper = new IndexWriterWrapper(index);
        writerWrapper.setIndexCreationEvents(indexCreationEvents);
        for (int i = 0; i < writerThreads; i++) {
            startConsumer(index, spec.writeJobs, i, writerWrapper);
        }

        for (int i = 0; i < loaderThreads; i++) {
            startProducer(index, spec.loadJobs, i);
        }

        startOptimizer(index, spec, writerWrapper);
        log.info("started " + index + " threads: 1 optimizer, " + writerThreads + " writers, " + loaderThreads + " loaders");
    }

    private void startOptimizer(Index index, IndexSpec spec, IndexWriterWrapper writerWrapper) {
        Optimizer optimizer = new Optimizer(index, writerWrapper);
        taskProcessors.add(optimizer);
        new Thread(taskThreads, optimizer, optimizer.toString()).start();
        spec.writeJobs.setObserver(optimizer);
    }

    private void startConsumer(Index index, JobQueue jobs, int i, IndexWriterWrapper writerWrapper) throws IOException {
        LuceneConsumer consumer = new LuceneConsumer(jobs, index, i, writerWrapper, indexCreationEvents);
        taskProcessors.add(consumer);
        new Thread(taskThreads, consumer, consumer.toString()).start();
    }

    private void startProducer(Index index, JobQueue jobs, int i) {
        LuceneProducer producer = new LuceneProducer(jobs, i, index, hibernate, indexCreationEvents);
        taskProcessors.add(producer);
        new Thread(taskThreads, producer, producer.toString()).start();
    }

    private Class[] loadClasses(String classList) throws ClassNotFoundException {
        String[] optionString = classList.split(",");
        List<Class> result = new LinkedList<Class>();
        for (String name : optionString) {
            name = name.trim();
            result.add(Class.forName(name));
        }
        return result.toArray(new Class[result.size()]);
    }

    public void setIndexRootDirectory(String indexRootDirectory) {
        this.indexRootDirectory = indexRootDirectory;
    }

    public void setWriteJobLimit(int writeJobLimit) {
        this.writeJobLimit = writeJobLimit;
    }

    public Object getObject() throws Exception {
        return indexGroup;
    }

    public Class getObjectType() {
        return Indices.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setHibernate(SessionOperationRunner hibernate) {
        this.hibernate = hibernate;
    }

    public void destroy() {
        log.info("stopping all indexing threads");
        taskThreads.interrupt();
        log.info("cancelling all indexing jobs");
        indexGroup.cancelAll();
        log.info("requesting all producers/consumers to stop");
        for (IndexTaskProcessor taskProcessor : taskProcessors) {
            taskProcessor.stop();
        }
        log.info("destroy: done");
    }

    public void setIndexCreationEvents(IndexCreationEvents indexCreationEvents) {
        this.indexCreationEvents = indexCreationEvents;
    }
}
