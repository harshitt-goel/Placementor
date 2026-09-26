package com.placementor.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PlacementorBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlacementorBackendApplication.class, args);
    }
}
