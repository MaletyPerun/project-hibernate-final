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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static java.util.Objects.nonNull;

public class AppConfig {
    private final SessionFactory sessionFactory;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final RedisClient redisClient;
    private final ObjectMapper mapper;

    private final String propSource;
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig(String propSource) {
        logger.info("init app config");
        sessionFactory = prepareRelationalDb();
        cityRepository = new CityRepository(sessionFactory);
        countryRepository = new CountryRepository(sessionFactory);
        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
        this.propSource = propSource;
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

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(propSource)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String host = properties.getProperty("application.redis.client");
        String port = properties.getProperty("application.redis.port");
        int portInt = Integer.parseInt(port);

        RedisClient redisClient = RedisClient.create(RedisURI.create(host, portInt));
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
