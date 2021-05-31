package com.example.study.ecommerce.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

import com.example.study.ecommerce.domain.Cart;
import com.example.study.ecommerce.domain.CartRepository;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final ItemRepository itemRepository;
	private final CartRepository cartRepository;

	@GetMapping
	public Mono<Rendering> home() {
		return Mono.just(Rendering.view("home.html")
			.modelAttribute("items", this.itemRepository.findAll())
			.modelAttribute("cart", this.cartRepository.findById("My Cart")
				.defaultIfEmpty(new Cart("My Cart")))
			.build());
	}
}
