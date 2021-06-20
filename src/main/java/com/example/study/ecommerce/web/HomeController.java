package com.example.study.ecommerce.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.Rendering;

import com.example.study.ecommerce.application.InventoryService;
import com.example.study.ecommerce.domain.Cart;
import com.example.study.ecommerce.domain.Item;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final InventoryService inventoryService;

	@GetMapping
	public Mono<Rendering> home() {
		return Mono.just(Rendering.view("home.html")
			.modelAttribute("items", this.inventoryService.getInventory())
			.modelAttribute("cart", this.inventoryService.getCart("My Cart")
				.defaultIfEmpty(new Cart("My Cart")))
			.build());
	}

	@PostMapping
	@ResponseBody
	Mono<Item> createItem(@RequestBody Item newItem) {
		return this.inventoryService.saveItem(newItem);
	}

	@PostMapping("/add/{id}")
	public Mono<String> addToCart(@PathVariable String id) {
		return this.inventoryService.addItemToCart("My Cart", id)
			.thenReturn("redirect:/");
	}

	@GetMapping("/search")
	public Mono<Rendering> search(
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String description,
		@RequestParam boolean useAnd) {
		return Mono.just(Rendering.view("home.html")
			.modelAttribute("results",
				this.inventoryService.searchByExample(name, description, useAnd))
			.build());
	}
}
