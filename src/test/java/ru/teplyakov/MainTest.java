package ru.teplyakov;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import ru.teplyakov.domain.City;
import ru.teplyakov.domain.Country;
import ru.teplyakov.domain.CountryLanguage;
import ru.teplyakov.redis.CityCountry;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    public static SessionFactory sessionFactory;
    public static Session session = null;

    @BeforeAll
    static void setup(){
        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
//        try {
//            StandardServiceRegistry standardRegistry  = new StandardServiceRegistryBuilder()
//                    .configure("hibernate-test.cfg.xml").build();
//
//            Metadata metadata = new MetadataSources(standardRegistry)
//                    .addAnnotatedClass(Employee.class)
//                    .getMetadataBuilder()
//                    .build();
//
//            sessionFactory = metadata.getSessionFactoryBuilder().build();
//
//        } catch (Throwable ex) {
//            throw new ExceptionInInitializerError(ex);
//        }
    }

    @BeforeEach
    void setupThis(){
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    @AfterEach
    void tearThis(){
        session.getTransaction().commit();
    }

    @AfterAll
    static void tear(){
        sessionFactory.close();
    }

    @Test
    void createSessionFactoryWithXML() {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);
        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);

        //закроем текущую сессию, чтоб точно делать запрос к БД, а не вытянуть данные из кэша
//        main.sessionFactory.getCurrentSession().close();

        //выбираем случайных 10 id городов
        //так как мы не делали обработку невалидных ситуаций, используй существующие в БД id
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
//        main.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
//        main.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

//        main.shutdown();
    }

    public void testRedisData(List<Integer> ids) {
//        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
//            RedisStringCommands<String, String> sync = connection.sync();
//            for (Integer id : ids) {
//                String value = sync.get(String.valueOf(id));
//                try {
//                    mapper.readValue(value, CityCountry.class);
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public void testMysqlData(List<Integer> ids) {
//        try (Session session = sessionFactory.getCurrentSession()) {
//            session.beginTransaction();
//            for (Integer id : ids) {
//                City city = cityRepository.getById(id);
//                Set<CountryLanguage> languages = city.getCountry().getLanguages();
//            }
//            session.getTransaction().commit();
//        }
    }
}