package ru.teplyakov.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teplyakov.AppConfig;
import ru.teplyakov.domain.City;
import ru.teplyakov.domain.Country;
import ru.teplyakov.domain.CountryLanguage;
import ru.teplyakov.repository.redis.CityCountry;
import ru.teplyakov.repository.redis.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static List<CityCountry> transformData(List<City> cities) {
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

    public static List<City> fetchData(AppConfig appConfig) {
        logger.info("fetch data");
        try (Session session = appConfig.getSessionFactory().getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            List<CityCountry> preparedData = transformData(allCities);
            pushToRedis(appConfig, preparedData);
            session.beginTransaction();

            List<Country> countries = appConfig.getCountryRepository().getAll();
            int totalCount = appConfig.getCityRepository().getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(appConfig.getCityRepository().getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }

    public static void pushToRedis(AppConfig appConfig, List<CityCountry> data) {
        logger.info("push to redis");
        try (StatefulRedisConnection<String, String> connection = appConfig.getRedisClient().connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), appConfig.getMapper().writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
