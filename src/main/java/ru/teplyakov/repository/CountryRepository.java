package ru.teplyakov.repository;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teplyakov.domain.Country;

import java.util.List;

public class CountryRepository {
    private final SessionFactory sessionFactory;

    private static final Logger logger = LoggerFactory.getLogger(CountryRepository.class);

    public CountryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Country> getAll() {
        logger.info("get all");
        Query<Country> query = sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class);
        return query.list();
    }
}
