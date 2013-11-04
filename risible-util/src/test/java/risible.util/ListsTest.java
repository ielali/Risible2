package risible.util;

import junit.framework.TestCase;

import java.util.*;

public class ListsTest extends TestCase {
    private Long[] obj = new Long[10];

    protected void setUp() throws Exception {
        for (int i = 0; i < obj.length; i++) {
            obj[i] = new Random().nextLong();
        }
    }

    public void testSplitsOddSizeListIntoThreeColumns() {
        List right = splitAndTestLeftAndMiddleColumns(new ArrayList(Arrays.asList(obj[0], obj[1], obj[2], obj[3], obj[4])));
        assertEquals(1, right.size());
        assertEquals(obj[4], right.get(0));
    }

    public void testSplitsEvenSizeListIntoThreeColumns() {
        List right = splitAndTestLeftAndMiddleColumns(new ArrayList(Arrays.asList(obj[0], obj[1], obj[2], obj[3], obj[4], obj[5])));
        assertEquals(2, right.size());
        assertEquals(obj[4], right.get(0));
        assertEquals(obj[5], right.get(1));
    }

    public void testFindsAllFooInstancesWhereBarIsTrue() {
        Collection<Foo> foos = Lists.build(new Foo(1, false), new Foo(2, true), new Foo(3, true), new Foo(4, false));
        Collection<Foo> trueFoos = Lists.findAll(foos, "bar");
        assertEquals("[2, 3]", trueFoos.toString());
    }

    public void testFindsFirstFooInstancesWhereBarIsTrue() {
        Collection<Foo> foos = Lists.build(new Foo(1, false), new Foo(2, true), new Foo(3, true), new Foo(4, false));
        Foo trueFoo = Lists.findFirst(foos, "bar");
        assertEquals("2", trueFoo.toString());
    }

    public void testReturnsNullIfAllBarsAreFalse() {
        Collection<Foo> foos = Lists.build(new Foo(1, false), new Foo(2, false), new Foo(3, false), new Foo(4, false));
        assertNull(Lists.findFirst(foos, "bar"));
        assertTrue(Lists.findAll(foos, "bar").isEmpty());
    }

    public void testCreatesBatchesOfSizeFive() {
        List c = new ArrayList(Arrays.asList(obj));
        List<List> batches = Lists.splitIntoBatches(c, 5);
        assertEquals(2, batches.size());
        assertEquals(5, batches.get(0).size());
        assertEquals(5, batches.get(1).size());
    }

    public void testCreatesBatchesOfSizeSix() {
        List c = new ArrayList(Arrays.asList(obj));
        List<List> batches = Lists.splitIntoBatches(c, 6);
        assertEquals(2, batches.size());
        assertEquals(6, batches.get(0).size());
        assertEquals(4, batches.get(1).size());
    }

    public void testCreatesBatchesOfSizeThree() {
        List c = new ArrayList(Arrays.asList(obj));
        List<List> batches = Lists.splitIntoBatches(c, 3);
        assertEquals(4, batches.size());
        assertEquals(3, batches.get(0).size());
        assertEquals(3, batches.get(1).size());
        assertEquals(3, batches.get(2).size());
        assertEquals(1, batches.get(3).size());
    }

    public void testCreatesBatchesOfSizeNine() {
        List c = new ArrayList(Arrays.asList(obj));
        List<List> batches = Lists.splitIntoBatches(c, 9);
        assertEquals(2, batches.size());
        assertEquals(9, batches.get(0).size());
        assertEquals(1, batches.get(1).size());
    }

    public void testCreatesBigBatchesFromLargePool() {
        List c = createListOfSize(96513);
        List<List> batches = Lists.splitIntoBatches(c, 4000);
        assertEquals(25, batches.size());
        for (int i = 0; i < 24; i++) {
            assertEquals(4000, batches.get(i).size());
        }
        assertEquals(513, batches.get(24).size());
    }

    private List createListOfSize(int size) {
        List list = new LinkedList();
        for (int i = 0; i < size; i++) {
            list.add(new Object());
        }
        return list;
    }

    class Foo {
        private int id;
        private boolean bar;

        public Foo(int id, boolean bar) {
            this.id = id;
            this.bar = bar;
        }

        public boolean isBar() {
            return bar;
        }

        public String toString() {
            return String.valueOf(id);
        }
    }

    private List splitAndTestLeftAndMiddleColumns(List original) {
        List split = Lists.split(original, 3);

        assertEquals(3, split.size());
        List left = (List) split.get(0);
        List middle = (List) split.get(1);
        List right = (List) split.get(2);

        assertEquals(2, left.size());
        assertEquals(obj[0], left.get(0));
        assertEquals(obj[1], left.get(1));

        assertEquals(2, middle.size());
        assertEquals(obj[2], middle.get(0));
        assertEquals(obj[3], middle.get(1));
        return right;
    }
}
