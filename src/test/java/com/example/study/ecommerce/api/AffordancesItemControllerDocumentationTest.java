package com.example.study.ecommerce.api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = AffordancesItemController.class)
@AutoConfigureRestDocs
class AffordancesItemControllerDocumentationTest {

	@Autowired
	private WebTestClient client;

	@MockBean
	private ItemRepository repository;

	@Test
	void findSingleItemAffordances() {
		when(repository.findById("item-1"))
			.thenReturn(Mono.just(new Item("item-1", "Alf alarm clock",
									"nothing I really need", 19.99)));

		this.client.get().uri("/affordances/items/item-1")
			.accept(MediaTypes.HAL_FORMS_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(document("single-item-affordances", preprocessResponse(prettyPrint())));
	}

	@Test
	@WithMockUser(username = "alice", roles = {"SOME_OTHER_ROLE"})
	void addingInventoryWithoutProperRoleFails() {
		this.client
			.post().uri("/affordances/items/add")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue("{\"name\":\"iPhone12\", \"description\":\"upgrade\",\"price\":99.99}")
			.exchange()
			.expectStatus().isForbidden();
	}

	@Test
	@WithMockUser(username = "bob", roles = {"INVENTORY"})
	void addingInventoryWithProperRoleSucceeds() {
		this.client
			.post().uri("/affordances/items/add")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue("{\"name\":\"iPhone12\", \"description\":\"upgrade\",\"price\":99.99}")
			.exchange()
			.expectStatus().isCreated();

		this.repository.findByName("iPhone12")
			.as(StepVerifier::create)
			.expectNextMatches(item -> {
				assertThat(item.getId()).isNotEmpty();
				assertThat(item.getDescription()).isEqualTo("upgrade");
				assertThat(item.getPrice()).isEqualTo(99.99);
				return true;
			})
			.verifyComplete();
	}

}