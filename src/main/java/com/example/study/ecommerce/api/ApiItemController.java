package com.example.study.ecommerce.api;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ApiItemController {

	private final ItemRepository itemRepository;

	@GetMapping("/api/items")
	public Flux<Item> findAll() {
		return this.itemRepository.findAll();
	}

	@GetMapping("/api/items/{id}")
	public Mono<Item> findById(@PathVariable String id) {
		return this.itemRepository.findById(id);
	}

	@PostMapping("/api/items")
	public Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {
		return item.flatMap(this.itemRepository::save)
			.map(savedItem ->
				ResponseEntity.created(URI.create("/api/items/" + savedItem.getId()))
					.body(savedItem));
	}

	@PutMapping("/api/items/{id}")
	public Mono<ResponseEntity<?>> updateItem(
		@RequestBody Mono<Item> item, @PathVariable String id) {
		return item.map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice()))
			.flatMap(this.itemRepository::save)
			.map(ResponseEntity::ok);
	}
}
