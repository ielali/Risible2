package risible.lucene;

import org.apache.lucene.search.Query;

public interface IndexAccessController {
    Query constrain(Index index, Query original);
}
