package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import java.util.Properties;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");
        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    public void whenSaveEqualsUsersThenGetEmptyOptional() {
        var u1 = new User(0, "test@mail.ru", "name1", "123");
        var u2 = new User(1, "test@mail.ru", "name2", "1234");
        assertThat(sql2oUserRepository.save(u1).get()).usingRecursiveComparison().isEqualTo(u1);
        assertThat(sql2oUserRepository.save(u2)).isEqualTo(empty());
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(0, "test1@mail.ru", "name3", "123")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword("test1@mail.ru", "123").get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenDontSaveThenGetEmptyOptional() {
        var userOptional = sql2oUserRepository.findByEmailAndPassword("test2@mail.ru", "123");
        assertThat(userOptional).isEqualTo(empty());
    }
}