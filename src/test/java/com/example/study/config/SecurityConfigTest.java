package com.example.study.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.study.ecommerce.domain.ItemRepository;

@SpringBootTest
@AutoConfigureWebTestClient
class SecurityConfigTest {

	@Autowired
	private WebTestClient client;

	@Test
	@DisplayName("인가 실패 테스트")
	@WithMockUser(username = "alice", roles = {"SOME_OTHER_ROLE"})
	void addingInventoryWithoutProperRoleFails() {
		this.client.post().uri("/")
			.exchange()
			.expectStatus().isForbidden();
	}

}