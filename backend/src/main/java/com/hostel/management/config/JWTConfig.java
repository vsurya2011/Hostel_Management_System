package com.hostel.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import lombok.Getter;
import lombok.Setter;

/**
 * Central place for JWT-related configuration properties and
 * enabling JPA auditing (createdAt/updatedAt on BaseEntity).
 */
@Getter
@Setter
@Configuration
@EnableJpaAuditing
@ConfigurationProperties(prefix = "jwt")
public class JWTConfig {

    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;
}
