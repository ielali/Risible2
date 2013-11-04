package risible.lucene;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagedListTest extends TestCase {

    protected void setUp() throws Exception {
    }

    public void testConstructorThrowsExceptionIfListIsNull() throws Exception {
        try {
            new PagedList(null, 20, 1);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testConstructorThrowsExceptionIfPageSizeIsLowerThanOne() throws Exception {
        try {
            new PagedList(Collections.EMPTY_LIST, 0, 1);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testPageIndexResetToOneIfSetToLessThanOne() throws Exception {
        PagedList list = new PagedList(Collections.EMPTY_LIST, 20, -1);
        assertEquals(1, list.getCurrentPage());
    }

    public void testPageIndexResetToPagesCountIfSetToMoreThanPagesCount() throws Exception {
        PagedList list = new PagedList(generateList(50), 20, 5);
        assertEquals(3, list.getCurrentPage());
    }

    public void testWithEmptyList() throws Exception {
        PagedList list = new PagedList(Collections.EMPTY_LIST, 20, 1);
        assertEquals(1, list.getCurrentPage());
        assertEquals(1, list.getPageCount());
        assertEquals(0, list.getElementCount());
        assertFalse(list.hasNext());
        assertFalse(list.hasPrevious());
        assertEquals(0, list.size());
    }

    public void testFirstPage() throws Exception {
        PagedList list = new PagedList(generateList(11), 5, 1);
        assertEquals(1, list.getCurrentPage());
        assertEquals(3, list.getPageCount());
        assertEquals(11, list.getElementCount());
        assertTrue(list.hasNext());
        assertFalse(list.hasPrevious());
        assertEquals(5, list.size());
        assertEquals("[0, 1, 2, 3, 4]", list.getList().toString());
    }

    public void testLastPage() throws Exception {
        PagedList list = new PagedList(generateList(11), 5, 3);
        assertEquals(3, list.getCurrentPage());
        assertEquals(3, list.getPageCount());
        assertEquals(11, list.getElementCount());
        assertFalse(list.hasNext());
        assertTrue(list.hasPrevious());
        assertEquals(1, list.size());
        assertEquals("[10]", list.getList().toString());
    }

    public void testPageCount() throws Exception {
        PagedList list = new PagedList(generateList(60), 20, 3);
        assertEquals(3, list.getPageCount());
        list = new PagedList(generateList(61), 20, 3);
        assertEquals(4, list.getPageCount());
        list = new PagedList(generateList(0), 20, 3);
        assertEquals(1, list.getPageCount());
    }

    public void testNbElementsOnLastPage() {
        PagedList list = new PagedList(generateList(60), 20, 3);
        assertEquals(20, list.size());
        list = new PagedList(generateList(61), 20, 4);
        assertEquals(1, list.size());
        list = new PagedList(generateList(59), 20, 3);
        assertEquals(19, list.size());
    }

    public void testNbElementsOnFirstPage() {
        PagedList list = new PagedList(generateList(60), 20, 1);
        assertEquals(20, list.size());
        list = new PagedList(generateList(20), 20, 1);
        assertEquals(20, list.size());
        list = new PagedList(generateList(10), 20, 1);
        assertEquals(10, list.size());
        list = new PagedList(generateList(0), 20, 1);
        assertEquals(0, list.size());
    }

    public void testMiddlePage() throws Exception {
        PagedList list = new PagedList(generateList(11), 5, 2);
        assertEquals(2, list.getCurrentPage());
        assertEquals(3, list.getPageCount());
        assertEquals(11, list.getElementCount());
        assertTrue(list.hasNext());
        assertTrue(list.hasPrevious());
        assertEquals(5, list.size());
        assertEquals("[5, 6, 7, 8, 9]", list.getList().toString());
    }

    private List generateList(int size) {
        List list = new ArrayList();
        for (int i = 0; i < size; i++) {
            list.add(new Integer(i));
        }
        return list;
    }

}
