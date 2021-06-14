package com.example.study.ecommerce.message;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ContextConfiguration
@ActiveProfiles(value = {"test"})
public class RabbitTest {

	@Container
	static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private ItemRepository itemRepository;

	@DynamicPropertySource
	static void configure(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
		registry.add("spring.rabbitmq.port", container::getAmqpPort);
	}

	@Test
	void verifyMessagingThroughAmqp() throws InterruptedException {
		this.webTestClient.post().uri("/items")
			.bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
			.exchange()
			.expectStatus().isCreated()
			.expectBody();

		Thread.sleep(1500L);

		this.webTestClient.post().uri("/items")
			.bodyValue(new Item("Smurf TV tray", "nothing important", 29.99))
			.exchange()
			.expectStatus().isCreated()
			.expectBody();

		Thread.sleep(2000L);

		this.itemRepository.findAll()
			.as(StepVerifier::create)
			.expectNextMatches(item -> {
				assertThat(item.getName()).isEqualTo("Alf alarm clock");
				assertThat(item.getDescription()).isEqualTo("nothing important");
				assertThat(item.getPrice()).isEqualTo(19.99);
				return true;
			})
			.expectNextMatches(item -> {
				assertThat(item.getName()).isEqualTo("Smurf TV tray");
				assertThat(item.getDescription()).isEqualTo("nothing important");
				assertThat(item.getPrice()).isEqualTo(29.99);
				return true;
			})
			.verifyComplete();
	}
}
