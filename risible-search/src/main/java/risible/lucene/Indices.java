package risible.lucene;

public interface Indices {
    Index getIndex(String name);

    Index[] all();
}
