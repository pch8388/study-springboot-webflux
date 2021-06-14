package com.example.study.ecommerce.application;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpringAmqpItemService {
	private final ItemRepository repository;

	@RabbitListener(
		ackMode = "MANUAL",
		bindings = @QueueBinding(
			value = @Queue,
			exchange = @Exchange("hacking-spring-boot"),
			key = "new-items-spring-amqp"))
	public Mono<Void> processNewItemsViaSpringAmqp(Item item) {
		log.debug("Consuming => " + item);
		return this.repository.save(item).then();
	}
}
