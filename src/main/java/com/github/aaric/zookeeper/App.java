package com.github.aaric.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Launcher.
 *
 * @author Aaric, created on 2018-03-15T10:12.
 * @since 0.0.2-SNAPSHOT
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("App start...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("App stopped...");
        }));
    }
}
