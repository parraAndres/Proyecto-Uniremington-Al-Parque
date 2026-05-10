package com.vetsync.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
        "com.vetsync.app.repository",
        "com.vetsync.app.uniremington.repository",
        "com.uniremington.alparque.repository"
})
@EntityScan(basePackages = {
        "com.vetsync.app.entity",
        "com.vetsync.app.uniremington.entity",
        "com.uniremington.alparque.model"
})
@SpringBootApplication(scanBasePackages = {
        "com.vetsync.app",
        "com.uniremington.alparque"
})
public class VetSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(VetSyncApplication.class, args);
    }
}