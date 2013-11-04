package risible.lucene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagedList {
    public static final int DEFAULT_PAGE_SIZE = 20;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int pageIndex = 1;
    private int totalElementsCount = 0;
    private List list = new ArrayList();

    protected PagedList() {
        // Used only by subClasses
    }

    public PagedList(List elements, int pageSize, int pageIndex) {
        if (elements == null || pageSize < 1) {
            throw new IllegalArgumentException();
        }

        init(elements.size(), pageSize, pageIndex);

        for (int i = getIndexOffset(); i < getIndexOffset() + getElementCountForCurrentPage(); i++) {
            add(elements.get(i));
        }
    }

    protected void add(Object o) {
        list.add(o);
    }

    public int size() {
        return list.size();
    }

    public int getCurrentPage() {
        return pageIndex;
    }

    public int getPageCount() {
        return Math.max(1, (int) Math.ceil((float) totalElementsCount / (float) pageSize));
    }

    public boolean hasNext() {
        return pageIndex < getPageCount();
    }

    public boolean hasPrevious() {
        return pageIndex > 1;
    }

    public int getElementCount() {
        return totalElementsCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    protected int getIndexOffset() {
        return (this.pageIndex - 1) * this.pageSize;
    }

    protected int getElementCountForCurrentPage() {
        int result = this.pageSize;
        if (this.pageIndex == getPageCount()) {
            if (totalElementsCount / this.pageSize == this.pageIndex) {
                result = this.pageSize;
            } else {
                result = totalElementsCount % this.pageSize;
            }
        }
        return result;
    }

    protected void init(int size, int pageSize, int pageIndex) {
        this.totalElementsCount = size;
        this.pageSize = pageSize;
        if (pageIndex < 1) {
            this.pageIndex = 1;
        } else if (pageIndex > getPageCount()) {
            this.pageIndex = getPageCount();
        } else {
            this.pageIndex = pageIndex;
        }
    }

    public List getList() {
        return Collections.unmodifiableList(list);
    }
}