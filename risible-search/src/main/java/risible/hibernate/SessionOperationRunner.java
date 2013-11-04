package risible.hibernate;

public interface SessionOperationRunner {
    Object inSession(boolean readOnly, Operation o) throws Exception;
}
