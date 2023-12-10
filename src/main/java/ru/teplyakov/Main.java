package ru.teplyakov;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.teplyakov.domain.City;
import ru.teplyakov.domain.Country;
import ru.teplyakov.domain.CountryLanguage;
import ru.teplyakov.repository.CityRepository;
import ru.teplyakov.repository.CountryRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class Main {

    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;

    private final ObjectMapper mapper;

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public Main() {
        sessionFactory = prepareRelationalDb();
        cityRepository = new CityRepository(sessionFactory);
        countryRepository = new CountryRepository(sessionFactory);

        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);
        main.shutdown();
    }

    private SessionFactory prepareRelationalDb() {
        return new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
    }

    private RedisClient prepareRedisClient() {
//        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
//        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
//            System.out.println("\nConnected to Redis\n");
//        }
//        return redisClient;
        return null;
    }

    private List<City> fetchData(Main main) {
        try (Session session = main.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();

            int totalCount = main.cityRepository.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityRepository.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

    private void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}