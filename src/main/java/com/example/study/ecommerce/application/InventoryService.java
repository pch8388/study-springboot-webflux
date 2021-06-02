package com.example.study.ecommerce.application;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class InventoryService {

	private final ItemRepository itemRepository;
	private final ReactiveFluentMongoOperations fluentMongoOperations;

	public Flux<Item> searchByExample(String name, String description, boolean useAnd) {
		Item item = new Item(name, description, 0.0);

		ExampleMatcher matcher = useAnd
			? ExampleMatcher.matchingAll()
			: ExampleMatcher.matchingAny()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
				.withIgnoreCase()
				.withIgnorePaths("price");

		Example<Item> probe = Example.of(item, matcher);

		return itemRepository.findAll(probe);
	}

	public Flux<Item> searchByFluentExample(String name, String description, boolean useAnd) {
		Item item = new Item(name, description, 0.0);

		ExampleMatcher matcher = useAnd
			? ExampleMatcher.matchingAll()
			: ExampleMatcher.matchingAny()
			.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
			.withIgnoreCase()
			.withIgnorePaths("price");

		return fluentMongoOperations.query(Item.class)
			.matching(query(byExample(Example.of(item, matcher))))
			.all();
	}

	public Flux<Item> searchByFluentExample(String name, String description) {
		return fluentMongoOperations.query(Item.class)
			.matching(query(where("TV tray").is(name).and("Smurf").is(description)))
			.all();
	}
}
