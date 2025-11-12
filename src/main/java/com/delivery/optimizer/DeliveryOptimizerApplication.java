package com.delivery.optimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class DeliveryOptimizerApplication {
    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module();
    }
    public static void main(String[] args) {
        SpringApplication.run(DeliveryOptimizerApplication.class, args);
    }
}
