/*
 * Copyright (c) 2026 haidarim
 * All rights reserved.
 *
 * This software is provided for personal, non-commercial use only.
 *
 * Unauthorized copying, modification, redistribution, or use in
 * commercial products or services is strictly prohibited.
 *
 * You may fork and modify this code solely for the purpose of
 * contributing bug fixes or improvements back to the original
 * repository via pull requests.
 *
 * All other uses require explicit written permission from the author.
 */

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
