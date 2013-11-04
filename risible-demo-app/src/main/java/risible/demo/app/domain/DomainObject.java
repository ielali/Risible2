package risible.demo.app.domain;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 10/15/13
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class DomainObject<T extends Serializable> {
    protected T id;

    @Access(value = AccessType.PROPERTY)
    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
