package com.example.study.ecommerce.api;

import java.net.URI;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.study.ecommerce.domain.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
@RestController
public class SpringAmqpItemController {

	private final AmqpTemplate template;

	@PostMapping("/items")
	public Mono<ResponseEntity<?>> addNewItemUsingSpringAmqp(@RequestBody Mono<Item> item) {
		return item
			.subscribeOn(Schedulers.boundedElastic())
			.flatMap(content ->
				Mono.fromCallable(() -> {
					this.template.convertAndSend(
						"hacking-spring-boot", "new-items-spring-amqp", content);
					return ResponseEntity.created(URI.create("/items")).build();
				}));
	}
}
