package ru.teplyakov.repository;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ru.teplyakov.domain.Country;

import java.util.List;

public class CountryRepository {
    private final SessionFactory sessionFactory;

    public CountryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Country> getAll() {
        Query<Country> query = sessionFactory.getCurrentSession().createQuery("select c from Country c", Country.class);
        return query.list();
    }
}
