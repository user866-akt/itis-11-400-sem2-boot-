package com.khubeev.repository;

import com.khubeev.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
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