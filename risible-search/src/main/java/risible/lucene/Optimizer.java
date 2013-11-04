package risible.lucene;

public class Optimizer implements Runnable, IndexTaskProcessor, JobObserver {
    private final Index index;
    private final IndexWriterWrapper writer;

    private boolean needsOptimisation = false;
    private boolean dead = false;

    public Optimizer(Index index, IndexWriterWrapper writer) {
        this.index = index;
        this.writer = writer;
    }

    public String toString() {
        return index + " Optimizer";
    }

    public void run() {
        while (!dead) {
            try {
                waitForOptimizationRequest();
                index.getStatus().waitUntilFinished();
                if (needsOptimisation) {
                    needsOptimisation = false;
                    optimize();
                }
            } catch (InterruptedException e) {
                System.out.println(index.getName() + " optimizer interrupted " + e);
            } finally {
                writer.close();
            }
        }
    }

    private synchronized void waitForOptimizationRequest() throws InterruptedException {
        while (!needsOptimisation) {
            wait(60000);
        }
    }

    private void optimize() throws InterruptedException {
        try {
            writer.optimize();
        } finally {
            writer.close();
        }
    }

    public void stop() {
        dead = true;
    }

    public synchronized void newJob(Object o) {
        needsOptimisation = true;
        notifyAll();
    }
}
