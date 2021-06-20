package com.example.study.config;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.study.ecommerce.domain.ItemRepository;

import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
class SecurityConfigTest {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ItemRepository repository;

	@Test
	@DisplayName("인가 실패 테스트")
	@WithMockUser(username = "alice", roles = {"SOME_OTHER_ROLE"})
	void addingInventoryWithoutProperRoleFails() {
		this.client.post().uri("/")
			.exchange()
			.expectStatus().isForbidden();
	}


	@Test
	@DisplayName("인가 성공 테스트")
	@WithMockUser(username = "bob", roles = {"INVENTORY"})
	void addingInventoryWithProperRoleSucceeds() {
		this.client.post().uri("/")
			.contentType(APPLICATION_JSON)
			.bodyValue("{\"name\":\"iPhone 12\", \"description\":\"upgrade\", \"price\":999.99}")
			.exchange()
			.expectStatus().isOk();

		this.repository.findByName("iPhone 12")
			.as(StepVerifier::create)
			.expectNextMatches(item -> {
				assertThat(item.getDescription()).isEqualTo("upgrade");
				assertThat(item.getPrice()).isEqualTo(999.99);
				return true;
			})
			.verifyComplete();
	}
}