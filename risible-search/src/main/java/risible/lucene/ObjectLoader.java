package risible.lucene;

import org.hibernate.ScrollableResults;

import java.util.Date;
import java.util.List;

public interface ObjectLoader {
    List findObjects(Class aClass, List ids);

    List findAllIds(Class aClass);

    List findUpdatedIds(Class clazz, Date updatedSince);

    void clearSession();

    ScrollableResults allInstances(Class clazz);
}
