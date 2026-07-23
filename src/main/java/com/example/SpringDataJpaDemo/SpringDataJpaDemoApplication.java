package com.example.SpringDataJpaDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Entry point of the Spring Boot application.
 *
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * - Starts the embedded Tomcat server
 * - Scans this package (and subpackages) for @Component / @Service / @RestController / etc.
 */
@SpringBootApplication
public class SpringDataJpaDemoApplication {

    /**
     * JVM starts here. SpringApplication.run(...) boots the Spring context.
     * The BCrypt prints are only for generating seed password hashes (data.sql).
     */
    public static void main(String[] args) {

        System.out.println("Users password " + new BCryptPasswordEncoder().encode("user123"));
        System.out.println("Admin password " + new BCryptPasswordEncoder().encode("adminpass"));

        SpringApplication.run(SpringDataJpaDemoApplication.class, args);
    }

}
