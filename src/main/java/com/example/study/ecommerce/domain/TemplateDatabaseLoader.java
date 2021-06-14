package com.example.study.ecommerce.domain;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class TemplateDatabaseLoader {

	@Bean
	CommandLineRunner initialize(MongoOperations mongo) {
		return args -> {
			mongo.save(new Item("Alf alarm clock", 19.99));
			mongo.save(new Item("Smurf TV tray", 24.99));
		};
	}
}
