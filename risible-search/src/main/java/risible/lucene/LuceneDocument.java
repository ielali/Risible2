package risible.lucene;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import risible.util.Lists;
import risible.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static risible.lucene.LuceneDocument.Option.nogoogle;

public class LuceneDocument {
    public static enum Option {
        sort, nogoogle;

        public boolean isIn(Option... options) {
            return Lists.contains(this, options);
        }
    }

    private Document doc;
    private String prefix;
    private List<String> fieldNamesForFullTextIndexing;

    public LuceneDocument() {
        this(new Document());
    }

    public LuceneDocument(Document doc) {
        this(doc, null);
    }

    public LuceneDocument(Document doc, String prefix) {
        this.doc = doc;
        this.prefix = prefix;
    }

    public void index(String name, Long value) {
        index(name, value, (Float) null);
    }

    public void index(String name, Long value, Float boost) {
        index(name, value == null ? "" : value.toString(), boost);
    }

    public void index(String name, Long value, LuceneDocument.Option... options) {
        index(name, value == null ? "" : value.toString(), options);
    }

    public void index(String name, Boolean value) {
        index(name, Strings.toYorN(value), nogoogle);
    }

    public void index(String name, String value, LuceneDocument.Option... options) {
        index(name, value, (Float) null, options);
    }

    public void index(String name, String value, Float boost, LuceneDocument.Option... options) {
        index(name, value, boost, Field.Index.TOKENIZED, options);
    }

    public void index(String name, String value, Field.Index index, LuceneDocument.Option... options) {
        index(name, value, null, index, options);
    }

    public void index(String name, String value, Float boost, Field.Index index, LuceneDocument.Option... options) {
        if (value == null) {
            value = "";
        }

        String fullFieldName = concatenate(prefix, name);
        doc.add(new Field(fullFieldName, value, Field.Store.YES, index));

        if (Option.sort.isIn(options)) {
            doc.add(new Field(fullFieldName + "_sort", value, Field.Store.NO, Field.Index.UN_TOKENIZED));
        }

        if (shouldIndexFulltext(name, options)) {
            Field fullTextField = new Field("content", value, Field.Store.YES, Field.Index.TOKENIZED);
            if (boost != null) {
                fullTextField.setBoost(boost);
            }
            doc.add(fullTextField);
        }
    }

    private boolean shouldIndexFulltext(String name, Option... options) {
        return (!nogoogle.isIn(options)) && (fieldNamesForFullTextIndexing == null || fieldNamesForFullTextIndexing.contains(name));
    }

    public Object get(String fieldName) {
        String name = concatenate(prefix, fieldName);
        String value = doc.get(name);
        if (value != null) {
            return value;
        } else {
            List<Field> fields = doc.getFields();
            for (Field field : fields) {
                if (field.name().startsWith(name)) {
                    return new LuceneDocument(doc, name);
                }
            }

            return null;
        }
    }

    private String concatenate(String prefix, String fieldName) {
        if (StringUtils.isBlank(prefix)) {
            return fieldName;
        } else {
            return prefix + "." + fieldName;
        }
    }

    public LuceneDocument create(String fieldName) {
        if (fieldNamesForFullTextIndexing == null) {
            return new LuceneDocument(doc, concatenate(prefix, fieldName));
        } else {
            String[] subDocFulltextFields = collectFieldsWithPrefix(fieldName);
            return new LuceneDocument(doc, concatenate(prefix, fieldName)).includeFullText(subDocFulltextFields);
        }
    }

    private String[] collectFieldsWithPrefix(String fieldName) {
        if (fieldNamesForFullTextIndexing == null) {
            return new String[0];
        }

        List<String> subFields = new ArrayList<String>();
        for (String f : fieldNamesForFullTextIndexing) {
            if (f.startsWith(fieldName)) {
                subFields.add(f.substring(fieldName.length() + 1));
            }
        }
        return subFields.toArray(new String[subFields.size()]);
    }

    public Document getOriginal() {
        return doc;
    }

    public void populateFrom(Indexable object) {
        if (object != null) {
            object.populate(this);
        }
    }

    public LuceneDocument includeFullText(String... fieldNames) {
        if (this.fieldNamesForFullTextIndexing != null && this.fieldNamesForFullTextIndexing.size() == 0) return this;
        this.fieldNamesForFullTextIndexing = new ArrayList<String>(Arrays.asList(fieldNames));
        return this;
    }

    public LuceneDocument includeNoFullText() {
        this.fieldNamesForFullTextIndexing = new ArrayList<String>();
        return this;
    }

    public String toString() {
        return "[" + prefix + "]" + doc.toString();
    }
}
