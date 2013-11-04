package risible.demo.app;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import risible.demo.app.dao.Repository;
import risible.demo.app.domain.DomainObject;
import risible.demo.app.domain.User;

import javax.annotation.Resource;
import java.util.List;


@Component
public class UserService {
    @Resource
    Repository repository;

    @Transactional
    public <T extends DomainObject> void persist(T domainObject) {
        repository.persist(domainObject);
    }

    public User findUser(int id) {
        if (id < 0) {
            return null;
        }
        return repository.find(User.class, id);
    }

    public List<User> users() {
        return repository.getEntityManager().createQuery("from risible.demo.app.domain.User user").getResultList();
    }
}
