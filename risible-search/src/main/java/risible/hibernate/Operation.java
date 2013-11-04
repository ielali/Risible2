package risible.hibernate;

import org.hibernate.Session;

public interface Operation {
    Object run(Session s) throws Exception;
}
