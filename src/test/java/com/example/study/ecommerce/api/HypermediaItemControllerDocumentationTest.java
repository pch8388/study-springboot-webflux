package com.example.study.ecommerce.api;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers =  HypermediaItemController.class)
@AutoConfigureRestDocs
class HypermediaItemControllerDocumentationTest {

	@Autowired
	private WebTestClient client;

	@MockBean
	private ItemRepository repository;

	@Test
	void findOneItem() {
		when(repository.findById("item-1"))
			.thenReturn(Mono.just(new Item("item-1", "new item",
									"new item mock", 19.99)));

		this.client.get().uri("/hypermedia/items/item-1")
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.consumeWith(document("findOne-hypermedia", preprocessResponse(prettyPrint()),
							links(
								linkWithRel("self").description("이 `Item`에 대한 공식 링크"),
								linkWithRel("item").description("`Item` 목록 링크")))
			);
	}
}