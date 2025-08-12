package com.rentacar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RentACarApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentACarApplication.class, args);
    }
}
