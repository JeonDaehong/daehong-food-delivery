package com.example.makedelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MakeDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MakeDeliveryApplication.class, args);
    }

}
