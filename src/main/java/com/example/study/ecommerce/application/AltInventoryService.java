package com.example.study.ecommerce.application;

import org.springframework.stereotype.Service;

import com.example.study.ecommerce.domain.Cart;
import com.example.study.ecommerce.domain.CartItem;
import com.example.study.ecommerce.domain.CartRepository;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AltInventoryService {

	private final ItemRepository itemRepository;
	private final CartRepository cartRepository;

	public Mono<Cart> addItemToCart(String cartId, String itemId) {
		Cart myCart = this.cartRepository.findById(cartId)
			.defaultIfEmpty(new Cart(cartId))
			.block();

		return myCart.getCartItems().stream()
			.filter(cartItem -> cartItem.getItem().getId().equals(itemId))
			.findAny()
			.map(cartItem -> {
				cartItem.increment();
				return Mono.just(myCart);
			})
			.orElseGet(() -> itemRepository.findById(itemId)
				.map(CartItem::new)
				.map(cartItem -> {
					myCart.getCartItems().add(cartItem);
					// return cart;
					return myCart;
				})
			)
			.flatMap(cartRepository::save);

	}
}
