package com.example.superdupermart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class SuperDuperMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperDuperMartApplication.class, args);
    }

}
