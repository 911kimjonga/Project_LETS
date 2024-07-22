package com.vj.lets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FinalProjectLetsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProjectLetsApplication.class, args);
    }

}
