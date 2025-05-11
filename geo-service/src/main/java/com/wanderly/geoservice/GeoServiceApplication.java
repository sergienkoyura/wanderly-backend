package com.wanderly.geoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.wanderly.*")
@EnableCaching
public class GeoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeoServiceApplication.class, args);
    }

}
