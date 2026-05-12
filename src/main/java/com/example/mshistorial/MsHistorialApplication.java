package com.example.mshistorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsHistorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsHistorialApplication.class, args);
    }

}
