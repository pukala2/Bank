package com.bank.cards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@RefreshScope
@ComponentScan({"com.bank.cards.controller", "com.bank.cards.config"})
@EnableJpaRepositories("com.bank.cards.repository")
@EntityScan({"com.bank.cards.model", "com.bank.config.model"})
@SpringBootApplication

public class CardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsApplication.class, args);
	}

}
