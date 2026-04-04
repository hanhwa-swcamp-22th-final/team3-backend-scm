package com.ohgiraffers.team3backendscm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Team3BackendScmApplication {

    public static void main(String[] args) {
        SpringApplication.run(Team3BackendScmApplication.class, args);
    }

}
