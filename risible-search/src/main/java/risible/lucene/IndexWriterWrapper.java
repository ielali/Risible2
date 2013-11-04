package risible.lucene;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import risible.lucene.events.IndexCreationEvents;

import java.io.IOException;

class IndexWriterWrapper {
    private final Logger log;
    private final Index index;
    private IndexWriter writer;
    private IndexCreationEvents indexCreationEvents;

    IndexWriterWrapper(Index index) {
        this.index = index;
        this.log = Logger.getLogger(getClass().getName() + "." + index.getName());
    }

    synchronized void close() {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
            writer = null;
        } catch (IOException e) {
            log.error("Error closing writer ", e);
        }
        log.info("writer closed");
    }

    synchronized void optimize() {
        try {
            indexCreationEvents.postCreation(index.getName());
            indexCreationEvents.preOptimization(index.getName());
            getWriter().optimize();
            indexCreationEvents.postOptimization(index.getName());
            log.info("index optimized");
        } catch (IOException e) {
            log.error("Error optimizing", e);
        }
    }

    synchronized void updateDocument(Term term, Document doc) throws IOException {
        IndexWriter indexWriter;
        synchronized (this) {
            indexWriter = getWriter();
        }
        indexWriter.updateDocument(term, doc);
    }

    private IndexWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new IndexWriter(index.getIndexDirectory(), IndexingService.ANALYZER);
            writer.setMaxBufferedDeleteTerms(2000);
            writer.setMaxBufferedDocs(2000);
            writer.setMergeFactor(20);
            log.info("writer opened");
            indexCreationEvents.preCreation(index.getName());
        }
        return writer;
    }

    public void setIndexCreationEvents(IndexCreationEvents indexCreationEvents) {
        this.indexCreationEvents = indexCreationEvents;
    }
}
