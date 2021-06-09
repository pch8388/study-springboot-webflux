package com.example.study.config;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpTraceConfiguration {

	@Bean
	public HttpTraceRepository traceRepository() {
		return new InMemoryHttpTraceRepository();
	}
}
