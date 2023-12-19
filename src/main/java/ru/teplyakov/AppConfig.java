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
import ru.teplyakov.repository.CityRepository;
import ru.teplyakov.repository.CountryRepository;

import static java.util.Objects.nonNull;

public class AppConfig {
    private final SessionFactory sessionFactory;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final RedisClient redisClient;
    private final ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig() {
        logger.info("init app config");
        sessionFactory = prepareRelationalDb();
        cityRepository = new CityRepository(sessionFactory);
        countryRepository = new CountryRepository(sessionFactory);
        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
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

    public void close() {
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
