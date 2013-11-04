package risible.lucene;

public class PagedHitsList extends PagedList {

    public PagedHitsList(LuceneHits hits, int pageSize, int pageIndex) {
        if (hits == null || pageSize < 1) {
            throw new IllegalArgumentException();
        }

        init(hits.length(), pageSize, pageIndex);

        collectDocumentProxiesToAvoidOutOfMemoryError(hits);
    }

    private void collectDocumentProxiesToAvoidOutOfMemoryError(LuceneHits hits) {
        for (int i = getIndexOffset(); i < getIndexOffset() + getElementCountForCurrentPage(); i++) {
            add(new DocumentHitProxy(hits, i));
        }
    }

}
