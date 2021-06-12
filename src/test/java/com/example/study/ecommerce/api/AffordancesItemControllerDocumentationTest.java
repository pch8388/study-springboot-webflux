package com.example.study.ecommerce.api;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import reactor.core.publisher.Mono;

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

}