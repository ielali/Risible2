package risible.lucene;

public interface JobQueue {

    void setObserver(JobObserver observer);

    void put(Object job) throws InterruptedException;

    int count();

    void cancel();

    Object next(Object client) throws InterruptedException;

    void idle(Object client);
}
