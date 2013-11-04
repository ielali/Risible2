package risible.lucene;

import org.junit.Assert;
import org.junit.Test;
import risible.util.Lists;

public class IndexGroupFactoryBeanTest {

    @Test
    public void testCreatesIndexInstancesFromConfigProperties() throws Exception {
        String fooConfig = "name: foo\n" +
                "writerThreads: 0\n" +
                "loaderThreads: 0\n" +
                "classes: " + AnIndexableType.class.getName() + "\n" +
                "incremental: true";

        String barConfig = "name: bar\n" +
                "writerThreads: 0\n" +
                "loaderThreads: 0\n" +
                "classes: " + IndexableTypeThree.class.getName() + "\n" +
                "incremental: true";

        IndexGroupFactoryBean b = new IndexGroupFactoryBean();
        b.setConfigs(Lists.build(fooConfig, barConfig));
        b.setWriteJobLimit(10);
        b.afterPropertiesSet();

        Indices indices = (Indices) b.getObject();
        Assert.assertEquals(2, indices.all().length);
        Assert.assertNotNull(indices.getIndex("foo"));
        Assert.assertNotNull(indices.getIndex("bar"));
    }
}
