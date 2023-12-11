package ru.teplyakov;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teplyakov.domain.City;
import ru.teplyakov.domain.Country;
import ru.teplyakov.domain.CountryLanguage;
import ru.teplyakov.redis.CityCountry;
import ru.teplyakov.redis.Language;
import ru.teplyakov.repository.CityRepository;
import ru.teplyakov.repository.CountryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;
    private final ObjectMapper mapper;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public Main() {
        logger.info("initialization");
        sessionFactory = prepareRelationalDb();
        cityRepository = new CityRepository(sessionFactory);
        countryRepository = new CountryRepository(sessionFactory);

        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);
        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);
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
            System.out.println("\nConnected to Redis\n");
        }
        return redisClient;
    }

    List<City> fetchData(Main main) {
        logger.info("fetch data");
        try (Session session = main.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            List<CityCountry> preparedData = main.transformData(allCities);
            main.pushToRedis(preparedData);
            session.beginTransaction();

            List<Country> countries = main.countryRepository.getAll();
            int totalCount = main.cityRepository.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityRepository.getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

    void pushToRedis(List<CityCountry> data) {
        logger.info("push to redis");
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    List<CityCountry> transformData(List<City> cities) {
        logger.info("trans from data");
        return cities.stream().map(city -> {
            CityCountry cityCountry = new CityCountry();
            cityCountry.setId(city.getId());
            cityCountry.setName(city.getName());
            cityCountry.setDistrict(city.getDistrict());
            cityCountry.setPopulation(city.getPopulation());

            Country country = city.getCountry();
            cityCountry.setAlternativeCountryCode(country.getAlternativeCode());
            cityCountry.setContinent(country.getContinent());
            cityCountry.setCountryCode(country.getCode());
            cityCountry.setCountryName(country.getName());
            cityCountry.setCountryPopulation(country.getPopulation());
            cityCountry.setCountryRegion(country.getRegion());
            cityCountry.setCountrySurfaceArea(country.getSurfaceArea());
            Set<CountryLanguage> countryLanguages = country.getLanguages();
            Set<Language> languages = countryLanguages.stream().map(l -> {
                Language language = new Language();
                language.setLanguage(l.getLanguage());
                language.setOfficial(l.getOfficial());
                language.setPercentage(l.getPercentage());
                return language;
            }).collect(Collectors.toSet());
            cityCountry.setLanguages(languages);
            return cityCountry;
        }).collect(Collectors.toList());
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
}