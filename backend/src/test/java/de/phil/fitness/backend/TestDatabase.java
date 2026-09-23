package de.phil.fitness.backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Supplies a PostgreSQL instance to every test that imports it.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestDatabase {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer("postgres:17");
    }
}
