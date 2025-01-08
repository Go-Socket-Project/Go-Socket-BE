package com.mycom.socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GoSocketBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoSocketBeApplication.class, args);
    }

}
