package risible.lucene;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import risible.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndexUnlockerTest {
    private IndexUnlocker unlocker;
    private Indices mockIndexGroup;
    private File directory;
    private Index index;

    @Before
    public void setUp() throws Exception {
        createDataDirectory();
        createIndex();
        createFiles();
        mockIndexGroup = mock(Indices.class);
        when(mockIndexGroup.all()).thenReturn(new Index[]{index});
        createUnlocker();
    }

    @Test
    public void testDoNothingIfLockFileNotPresent() throws Exception {
        checkIndexContent(10);
        unlocker.checkIndexes();
        checkIndexContent(10);
    }

    @Test
    public void testRemovesFilesIfLockFilePresentAndAskForReindex() throws Exception {
        createLockFile();
        checkIndexContent(11);
        unlocker.checkIndexes();
        Assert.assertFalse(directory.exists());
    }

    private void createUnlocker() {
        unlocker = new IndexUnlocker();
        unlocker.setIndexGroup(mockIndexGroup);
    }

    private void createFiles() throws IOException {
        for (int i = 0; i < 10; i++) {
            createFile(Long.toString(new Random().nextLong()));
        }
    }

    private void createDataDirectory() {
        File emptyDirectory = createEmptyDirectory(this.getClass());
        directory = Files.ensureDirectory(emptyDirectory.getAbsolutePath() + "/foo");
    }

    private static final File TEST_DIR = new File("tmp/test");

    public static File createEmptyDirectory(Class clazz) {
        File dir = new File(TEST_DIR, clazz.getSimpleName());
        Files.delete(dir);
        dir.mkdirs();
        return dir;
    }

    private void createIndex() {
        IndexSpec spec = new IndexSpec();
        spec.path = directory.getAbsolutePath();
        spec.name = "foo";
        index = new Index(spec);
    }

    private void createLockFile() throws IOException {
        createFile("write.lock");
    }

    private void createFile(String fileName) throws IOException {
        new File(directory, fileName).createNewFile();
    }

    private void checkIndexContent(int expectedFileCount) {
        Assert.assertEquals(expectedFileCount, directory.listFiles().length);
    }
}
