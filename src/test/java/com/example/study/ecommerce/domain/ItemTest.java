package com.example.study.ecommerce.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemTest {

	@Test
	void itemBasicsShouldWork() {
		Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);

		assertThat(sampleItem.getId()).isEqualTo("item1");
		assertThat(sampleItem.getName()).isEqualTo("TV tray");
		assertThat(sampleItem.getDescription()).isEqualTo("Alf TV tray");
		assertThat(sampleItem.getPrice()).isEqualTo(19.99);

		Item sameItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
		assertThat(sampleItem).isEqualTo(sameItem);
	}
}