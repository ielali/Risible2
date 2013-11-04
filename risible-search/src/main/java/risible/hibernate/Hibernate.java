package risible.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class Hibernate implements SessionOperationRunner {
    public static Session openHibernateSession(SessionFactory sessionFactory) {
        Session hibernateSession = SessionFactoryUtils.getSession(sessionFactory, true);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(hibernateSession));
        return hibernateSession;
    }

    public static void closeHibernateSession(Session session, SessionFactory sessionFactory) {
        TransactionSynchronizationManager.unbindResource(sessionFactory);
        SessionFactoryUtils.releaseSession(session, sessionFactory);
    }

    private SessionFactory sessionFactory;

    public Object inSession(boolean readOnly, Operation o) throws Exception {
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(readOnly);
        Session s = null;
        try {
            s = Hibernate.openHibernateSession(sessionFactory);
            if (readOnly) {
                s.setFlushMode(FlushMode.MANUAL);
            }
            return o.run(s);
        } finally {
            if (s != null) {
                Hibernate.closeHibernateSession(s, sessionFactory);
            }
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
