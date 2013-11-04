package risible.demo.app.dao;

import risible.demo.app.domain.DomainObject;
import risible.demo.app.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 10/23/13
 * Time: 2:36 AM
 * To change this template use File | Settings | File Templates.
 */
@org.springframework.stereotype.Repository
public class Repository {
    @PersistenceContext
    private EntityManager entityManager;

    public <T extends DomainObject> T find(Class<T> clazz, Serializable id) {
        return entityManager.find(clazz, id);
    }

    public <T extends DomainObject> void persist(T domainObject){
        entityManager.persist(domainObject);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
