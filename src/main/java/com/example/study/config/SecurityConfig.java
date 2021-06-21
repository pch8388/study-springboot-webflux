package com.example.study.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.study.user.domain.UserRepository;

@Configuration
@EnableReactiveMethodSecurity
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

	static final String USER = "USER";
	public static final String INVENTORY = "INVENTORY";

	@Bean
	public SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity http) {
		return http
			.authorizeExchange(exchanges -> exchanges
				.anyExchange().authenticated()
				.and()
				.httpBasic()
				.and()
				.formLogin())
			.csrf().disable()
			.build();
	}

	static String role(String auth) {
		return "ROLE_" + auth;
	}

	@Bean
	public CommandLineRunner userLoader(MongoOperations operations) {
		return args -> {
			operations.save(
				new com.example.study.user.domain.User("sc", "password",
					Collections.singletonList(role(USER))));

			operations.save(
				new com.example.study.user.domain.User("manager", "password",
					Arrays.asList(role(USER), role(INVENTORY))));

		};
	}
}
