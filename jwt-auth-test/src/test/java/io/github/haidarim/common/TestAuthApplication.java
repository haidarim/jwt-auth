package io.github.haidarim.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * To simulate a real client application
 */
@SpringBootApplication
public class TestAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestAuthApplication.class, args);
    }
}
