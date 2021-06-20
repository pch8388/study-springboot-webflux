package com.example.study.config;

import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;

import com.example.study.user.domain.UserRepository;

@Configuration
public class SecurityConfig {

	@Bean
	public ReactiveUserDetailsService userDetailsService(UserRepository repository) {
		return username -> repository.findByName(username)
			.map(user -> User.withDefaultPasswordEncoder()		// javadoc 참고 => deprecated , 샘플 프로젝트에서만 쓰라고 되어있음
				.username(user.getName())
				.password(user.getPassword())
				.authorities(user.getRoles().toArray(new String[0]))
				.build());
	}

	@Bean
	public CommandLineRunner userLoader(MongoOperations operations) {
		return args -> operations.save(
			new com.example.study.user.domain.User("sc", "password",
				Collections.singletonList("ROLE_USER")));
	}
}
