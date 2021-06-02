package com.example.study.ecommerce.domain;

import org.springframework.data.annotation.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Item {

	@Id
	private String id;
	private String name;
	private String description;
	private double price;

	private Item() {}

	public Item(String name, double price) {
		this(name, "", price);
	}

	public Item(String name, String description, double price) {
		this.name = name;
		this.description = description;
		this.price = price;
	}
}
