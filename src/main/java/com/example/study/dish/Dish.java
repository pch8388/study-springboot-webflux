package com.example.study.dish;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class Dish {

	@Setter
	private String description;
	private boolean delivered = false;

	public Dish(String description) {
		this.description = description;
	}

	public static Dish deliver(Dish dish) {
		Dish deliveredDish = new Dish(dish.description);
		deliveredDish.delivered = true;
		return deliveredDish;
	}
}
