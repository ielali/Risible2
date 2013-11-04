package risible.lucene;

import junit.framework.TestCase;
import org.apache.lucene.document.Field;

public class LuceneDocumentTest extends TestCase {
    private LuceneDocument doc;

    protected void setUp() throws Exception {
        super.setUp();
        doc = new LuceneDocument();
    }

    public void testBehavesJustLikeALuceneDocument() {
        doc.index("foo", "bar");
        assertEquals("bar", doc.get("foo"));
        assertEquals("bar", doc.get("content"));
    }

    public void testBehavesAsANestedStructure() {
        doc.index("town.location.country.id", "IN");
        LuceneDocument town = (LuceneDocument) doc.get("town");
        LuceneDocument location = (LuceneDocument) town.get("location");
        LuceneDocument country = (LuceneDocument) location.get("country");
        assertEquals("IN", country.get("id"));
    }

    public void testReturnsNullIfThereIsNoMatchingPrefix() {
        doc.index("town.location.country.id", "IN");
        assertNull(doc.get("owner"));
        LuceneDocument town = (LuceneDocument) doc.get("town");
        assertNull(town.get("state"));
    }

    public void testAddsAFieldToANestedDocument() {
        LuceneDocument town = doc.create("town");
        town.index("id", "DUB");
        assertEquals("DUB", doc.get("town.id"));
        assertEquals("DUB", doc.get("content"));
    }

    public void testCreatesContentFieldFromGivenSubsetOfFields() {
        LuceneDocument subDoc = doc.create("indexable").includeFullText("foo", "toto");
        indexFooTotoYadda(subDoc);
        Field[] contentFields = getContentField();
        assertEquals(2, contentFields.length);
        assertEquals("bar", contentFields[0].stringValue());
        assertEquals("titi", contentFields[1].stringValue());
    }

    public void testMultipleIndexingOrdersFirstIncludeNoFullTextWins() {
        LuceneDocument subDoc = doc.create("indexable").includeNoFullText();
        // this later call (maybe because of complexe tree) is completly ignored
        subDoc.includeFullText("foo");
        indexFooTotoYadda(subDoc);
        Field[] contentFields = getContentField();
        assertEquals(null, contentFields);
    }

    private Field[] getContentField() {
        return doc.getOriginal().getFields("content");
    }

    public void testCreatesContentFieldFromGivenSubsetOfFieldsInSubSubDocument() {
        LuceneDocument subDoc = doc.create("sub").includeFullText("abc", "subsub.foo", "subsub.toto");
        subDoc.index("abc", "123");
        subDoc.index("xyz", "321");

        LuceneDocument subSubDoc = subDoc.create("subsub");
        indexFooTotoYadda(subSubDoc);

        Field[] contentFields = getContentField();
        assertEquals(3, contentFields.length);
        assertEquals("123", contentFields[0].stringValue());
        assertEquals("bar", contentFields[1].stringValue());
        assertEquals("titi", contentFields[2].stringValue());
    }

    private void indexFooTotoYadda(LuceneDocument subSubDoc) {
        subSubDoc.index("foo", "bar");
        subSubDoc.index("toto", "titi");
        subSubDoc.index("yadda", "bang");
    }

    public void testDoesNotIndexFullTextIfRequested() {
        doc.index("foo", "bar", LuceneDocument.Option.nogoogle);
        doc.index("toto", "titi");

        Field[] contentFields = getContentField();
        assertEquals(1, contentFields.length);
        assertEquals("titi", contentFields[0].stringValue());
    }
}
