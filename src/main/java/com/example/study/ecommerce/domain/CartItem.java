package com.example.study.ecommerce.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
class CartItem {

	private Item item;
	private int quantity;

	private CartItem() {}

	CartItem(Item item) {
		this.item = item;
		this.quantity = 1;
	}
}
