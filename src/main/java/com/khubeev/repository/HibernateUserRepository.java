package com.khubeev.repository;

import com.khubeev.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernateUserRepository {

    private final SessionFactory sessionFactory;

    public HibernateUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<User> findAll() {
        return getCurrentSession().createQuery("from User", User.class).list();
    }

    public User findById(Long id) {
        return getCurrentSession().get(User.class, id);
    }

    public User save(User user) {
        getCurrentSession().persist(user);
        return user;
    }

    public User update(User user) {
        return getCurrentSession().merge(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            getCurrentSession().remove(user);
        }
    }
}