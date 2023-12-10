package com.example.superdupermart;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@EnableEncryptableProperties // key: secret
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class SuperDuperMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperDuperMartApplication.class, args);
    }

}
