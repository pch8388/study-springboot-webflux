package com.example.study.ecommerce.api;

import static com.example.study.config.SecurityConfig.*;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AffordancesItemController {

	private final ItemRepository itemRepository;

	@GetMapping("/affordances/items/{id}")
	public Mono<EntityModel<Item>> findOne(@PathVariable String id, Authentication auth) {
		AffordancesItemController controller = methodOn(AffordancesItemController.class);

		Mono<Link> selfLink = linkTo(controller.findOne(id, auth))
			.withSelfRel()
			.toMono();

		Mono<Link> aggregateLink = linkTo(controller.findAll(auth))
			.withRel(IanaLinkRelations.ITEM).toMono();

		return Mono.zip(itemRepository.findById(id), selfLink, aggregateLink)
			.map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));

	}

	@GetMapping("/affordances/items")
	public Mono<CollectionModel<EntityModel<Item>>> findAll(Authentication auth) {
		AffordancesItemController controller = methodOn(AffordancesItemController.class);

		Mono<Link> aggregateRoot = linkTo(controller.findAll(auth))
			.withSelfRel()
			.andAffordance(controller.addNewItem(null, auth))
			.toMono();

		return this.itemRepository.findAll()
			.flatMap(item -> findOne(item.getId(), auth))
			.collectList()
			.flatMap(models -> aggregateRoot.map(
				selfLink -> CollectionModel.of(models, selfLink)));
	}

	@PreAuthorize("hasRole('" + INVENTORY + "')")
	@PostMapping("/affordances/items/add")
	Mono<ResponseEntity<?>> addNewItem(@RequestBody Item item, Authentication auth) {
		return this.itemRepository.save(item)
			.map(Item::getId)
			.flatMap(id -> findOne(id, auth))
			.map(newModel -> ResponseEntity.created(newModel
				.getRequiredLink(IanaLinkRelations.SELF)
				.toUri()).build());
	}

	@PutMapping("/affordances/items/{id}")
	public Mono<ResponseEntity<?>> updateItem(
		@RequestBody Mono<EntityModel<Item>> item, @PathVariable String id,
		Authentication auth) {
		return item.map(EntityModel::getContent)
			.map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice()))
			.flatMap(this.itemRepository::save)
			.then(findOne(id, auth))
			.map(model -> ResponseEntity.noContent()
				.location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build()
			);
	}

	@PreAuthorize("hasRole('" + INVENTORY + "')")
	@DeleteMapping("/affordances/items/delete/{id}")
	public Mono<ResponseEntity<?>> deleteItem(@PathVariable String id) {
		return this.itemRepository.deleteById(id)
			.thenReturn(ResponseEntity.noContent().build());
	}
}
