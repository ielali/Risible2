package risible.lucene;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import risible.util.Lists;
import risible.util.Strings;

public class Queries {
    public static enum Option {
        no_strip;

        public boolean isIn(Option... options) {
            return Lists.contains(this, options);
        }
    }

    public static void buildFullTextQuery(BooleanQuery booleanQuery, String rawExpression, String fieldName, Option... options) {
        if (StringUtils.isBlank(rawExpression)) {
            return;
        }

        String stripped = rawExpression.toLowerCase();
        if (!Option.no_strip.isIn(options)) {
            stripped = Strings.stripNonWords(stripped);
        }
        String[] expressions = stripped.split(" ");
        for (String expr : expressions) {
            Query query;
            if (StringUtils.isNumeric(expr) || expr.length() < 3) {
                query = new TermQuery(new Term(fieldName, expr));
            } else {
                query = new WildcardQuery(new Term(fieldName, "*" + expr + "*"));
            }
            booleanQuery.add(query, BooleanClause.Occur.MUST);
        }
    }

}
