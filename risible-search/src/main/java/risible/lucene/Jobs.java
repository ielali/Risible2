package risible.lucene;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingDeque;

import java.util.Set;

public class Jobs implements JobQueue {
    private final BlockingQueue jobs;
    private JobObserver observer;
    private final Object lock;
    private final Set busyObjects;

    public Jobs(Object lock, int jobLimit, Set busyObjects) {
        this.lock = lock;
        this.busyObjects = busyObjects;
        this.jobs = new LinkedBlockingDeque(jobLimit);
    }

    public void setObserver(JobObserver observer) {
        this.observer = observer;
    }

    public void put(Object job) throws InterruptedException {
        jobs.put(job);
        if (observer != null) {
            observer.newJob(job);
        }
        notifyWaiters();
    }

    public int count() {
        return jobs.size();
    }

    public void cancel() {
        jobs.clear();
        notifyWaiters();
    }

    public Object next(Object client) throws InterruptedException {
        synchronized (lock) {
            Object job = jobs.poll();
            while (job == null) {
                lock.wait(5000);
                job = jobs.poll();
            }
            busyObjects.add(client);
            notifyWaiters();
            return job;
        }
    }

    private void notifyWaiters() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void idle(Object client) {
        synchronized (lock) {
            busyObjects.remove(client);
            notifyWaiters();
        }
    }
}
