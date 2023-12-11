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
import org.junit.jupiter.api.*;
import ru.teplyakov.domain.City;
import ru.teplyakov.domain.Country;
import ru.teplyakov.domain.CountryLanguage;
import ru.teplyakov.redis.CityCountry;
import ru.teplyakov.repository.CityRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    public static SessionFactory sessionFactory;
    public static Session session = null;
    public static RedisClient redisClient;
    public ObjectMapper mapper;
    public CityRepository cityRepository;

    @BeforeAll
    static void setup() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
    }

    @BeforeEach
    void setupThis() {
        session = sessionFactory.openSession();
        session.beginTransaction();
        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
        cityRepository = new CityRepository(sessionFactory);
    }

    @AfterEach
    void tearThis() {
        session.getTransaction().commit();
    }

    @AfterAll
    static void tear() {
        sessionFactory.close();
        redisClient.close();
    }

    public RedisClient prepareRedisClient() {
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("\nConnected to Redis\n");
        }
        return redisClient;
    }

    @Test
    @DisplayName("check time between redis and mysql")
    void checkTime() {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);
        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);

        //закроем текущую сессию, чтоб точно делать запрос к БД, а не вытянуть данные из кэша
        sessionFactory.getCurrentSession().close();

        //выбираем случайных 10 id городов
        //так как мы не делали обработку невалидных ситуаций, используй существующие в БД id
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        redisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        mysqlData(ids);
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

    public void mysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityRepository.getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }
}