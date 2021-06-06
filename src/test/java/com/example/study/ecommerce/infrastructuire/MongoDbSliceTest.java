package com.example.study.ecommerce.infrastructuire;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import reactor.test.StepVerifier;

@DataMongoTest
public class MongoDbSliceTest {

	@Autowired private ItemRepository itemRepository;

	@Test
	void saveItems() {
		Item saveItem = new Item("name", "description", 1.99);

		itemRepository.save(saveItem)
			.as(StepVerifier::create)
			.expectNextMatches(item -> {
				assertThat(item.getId()).isNotEmpty();
				assertThat(item.getName()).isEqualTo("name");
				assertThat(item.getDescription()).isEqualTo("description");
				assertThat(item.getPrice()).isEqualTo(1.99);

				return true;
			})
			.verifyComplete();
	}
}
