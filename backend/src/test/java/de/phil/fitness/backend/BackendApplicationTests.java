package de.phil.fitness.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
