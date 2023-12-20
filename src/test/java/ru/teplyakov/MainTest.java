package ru.teplyakov;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.junit.jupiter.api.*;
import ru.teplyakov.domain.City;
import ru.teplyakov.domain.CountryLanguage;
import ru.teplyakov.repository.CityRepository;
import ru.teplyakov.repository.redis.CityCountry;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.teplyakov.util.Util.*;

class MainTest {

    public static AppConfig testConfig;
    public static Session session;
    public static RedisClient redisClient;
    public ObjectMapper mapper;
    public CityRepository cityRepository;

    @BeforeAll
    static void setup() {
        testConfig = new AppConfig("src/test/resources/test.properties");
    }

    @BeforeEach
    void setupThis() {
        session = testConfig.getSessionFactory().openSession();
        session.beginTransaction();
        redisClient = testConfig.getRedisClient();
        mapper = testConfig.getMapper();
        cityRepository = testConfig.getCityRepository();
    }

    @AfterEach
    void tearThis() {
        session.getTransaction().commit();
    }

    @AfterAll
    static void tear() {
        testConfig.close();
    }

    @Test
    @DisplayName("check time between redis and mysql")
    void checkTime() {
        List<City> allCities = fetchData(testConfig);
        List<CityCountry> preparedData = transformData(allCities);
        pushToRedis(testConfig, preparedData);

        testConfig.getSessionFactory().getCurrentSession().close();

        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        redisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        mySqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        assertTrue(true);

    }

    public void redisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void mySqlData(List<Integer> ids) {
        try (Session session = testConfig.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityRepository.getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }
}