package ru.teplyakov;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teplyakov.domain.City;
import ru.teplyakov.domain.Country;
import ru.teplyakov.domain.CountryLanguage;
import ru.teplyakov.redis.CityCountry;
import ru.teplyakov.repository.CityRepository;
import ru.teplyakov.repository.CountryRepository;

import java.util.List;

import static java.util.Objects.nonNull;
import static ru.teplyakov.util.Util.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ObjectMapper mapper;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public Main() {
        logger.info("initialisation");
        sessionFactory = prepareRelationalDb();
        cityRepository = new CityRepository(sessionFactory);
        countryRepository = new CountryRepository(sessionFactory);

        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = fetchData(main);
        List<CityCountry> preparedData = transformData(allCities);
        pushToRedis(main, preparedData);
        main.shutdown();
    }

    private SessionFactory prepareRelationalDb() {
        logger.info("prepare relation DB");
        return new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
    }

    private RedisClient prepareRedisClient() {
        logger.info("prepare redis client");

        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            logger.info("\nConnected to Redis\n");
        }
        return redisClient;
    }

    private void shutdown() {
        logger.info("shutdown");
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public CityRepository getCityRepository() {
        return cityRepository;
    }

    public CountryRepository getCountryRepository() {
        return countryRepository;
    }
}