package com.example.study.ecommerce.blockhound;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.study.ecommerce.application.AltInventoryService;
import com.example.study.ecommerce.domain.Cart;
import com.example.study.ecommerce.domain.CartItem;
import com.example.study.ecommerce.domain.CartRepository;
import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class BlockHoundIntegrationTest {

	private AltInventoryService inventoryService;

	@MockBean
	private ItemRepository itemRepository;

	@MockBean
	private CartRepository cartRepository;

	@BeforeEach
	void setUp() {
		Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
		CartItem sampleCartItem = new CartItem(sampleItem);
		Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

		when(cartRepository.findById(anyString())).thenReturn(Mono.<Cart> empty().hide());

		when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
		when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

		inventoryService = new AltInventoryService(itemRepository, cartRepository);
	}

	@Test
	void blockHoundShouldTrapBlockingCall() {
		Mono.delay(Duration.ofSeconds(1))
			.flatMap(tick -> inventoryService.addItemToCart("My Cart", "item1"))
			.as(StepVerifier::create)
			.verifyErrorSatisfies(throwable -> assertThat(throwable)
				.hasMessageContaining("block()/blockFirst()/blockLast() are blocking"));
	}

}
