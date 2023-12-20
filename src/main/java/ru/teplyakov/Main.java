package ru.teplyakov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teplyakov.domain.City;
import ru.teplyakov.repository.redis.CityCountry;

import java.util.List;

import static java.util.Objects.nonNull;
import static ru.teplyakov.util.Util.*;

public class Main {

    // TODO: 20.12.2023 include properties in Docker
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static AppConfig appConfig = null;
    public static void init() {
        logger.info("initialisation");
        appConfig = new AppConfig("src/main/resources/application.properties");
    }

    public static void main(String[] args) {
        init();
        List<City> allCities = fetchData(appConfig);
        List<CityCountry> preparedData = transformData(allCities);
        pushToRedis(appConfig, preparedData);
        shutdown();
    }

    private static void shutdown() {
        logger.info("shutdown");
        if (nonNull(appConfig)) {
            appConfig.close();
        }
    }
}