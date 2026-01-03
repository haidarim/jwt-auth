package io.github.haidarim.common;

import io.github.haidarim.controller.TestAuthenticationController;
import io.github.haidarim.impl.DefaultJwtAuthenticationFilter;
import io.github.haidarim.impl.service.DefaultJwtService;
import io.github.haidarim.properties.JwtAuthProperties;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;


/**
 * Base class for Integration and System tests
 *  - Provides psql test container
 *  - Generates secure HS256 and RSA secrets  dynamically at test runtime using {@link org.springframework.test.context.DynamicPropertySource}
 */
@SpringBootTest(
        classes = {
                TestAuthApplication.class, // To tell spring boot application
                TestAppConfig.class, // To tell spring that scan this class first for beans then the app config
                DefaultJwtAuthenticationFilter.class,
                DefaultJwtService.class,
                JwtAuthProperties.class, // Note even @Component will not be auto loaded only AutoConfigure classes are loaded automatically
                TestAuthenticationController.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test") // to tell spring to use application-test.yml
@EnableJpaRepositories(basePackages = "io.github.haidarim.repository")
@EntityScan("io.github.haidarim.entity")
public class AbstractJwtTest {

    private static final String HS_SECRET = generateHS256Secret();
    private static final KeyPair RSA_KEYS = generateRsaKeys();
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");


    @LocalServerPort
    int port;

    protected WebTestClient webTestClient;



    static  {
        postgresContainer.start();
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    private static String generateHS256Secret(){
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    private static KeyPair generateRsaKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // to allow register or override application properties at test runtime before ApplicationContext is created
    // to avoid having some properties hard-codded in application properties
    @DynamicPropertySource
    static void registerJwtProperties(DynamicPropertyRegistry registry){
        registry.add("jwt.algorithm", ()-> "HS256");
        registry.add("jwt.hs_secret", ()-> HS_SECRET);
        registry.add("jwt.pr_secret", ()->base64(RSA_KEYS.getPrivate().getEncoded()));
        registry.add("jwt.pub_secret", ()-> base64(RSA_KEYS.getPublic().getEncoded()));
        registry.add("jwt.check_expiration", ()-> true);
        registry.add("jwt.expiration_time", ()->60000L);
    }

    private static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    @BeforeEach
    public void setup(){
        this.webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

}
