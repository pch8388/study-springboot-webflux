package com.example.study.ecommerce.application;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class InventoryService {

	private final ItemRepository itemRepository;

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

}
