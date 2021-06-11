package com.example.study.ecommerce.api;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.study.ecommerce.domain.Item;
import com.example.study.ecommerce.domain.ItemRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class HypermediaItemController {

	private final ItemRepository itemRepository;

	@GetMapping("/hypermedia")
	public Mono<RepresentationModel<?>> root() {
		HypermediaItemController controller = methodOn(HypermediaItemController.class);

		Mono<Link> selfLink = linkTo(controller.root()).withSelfRel().toMono();
		Mono<Link> itemsAggregateLink = linkTo(controller.findAll())
											.withRel(IanaLinkRelations.ITEM)
											.toMono();

		return selfLink.zipWith(itemsAggregateLink)
						.map(links -> Links.of(links.getT1(), links.getT2()))
						.map(links -> new RepresentationModel<>(links.toList()));
	}

	@GetMapping("/hypermedia/items")
	public Mono<CollectionModel<EntityModel<Item>>> findAll() {
		return this.itemRepository.findAll()
			.flatMap(item -> findOne(item.getId()))
			.collectList()
			.flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class))
										.withSelfRel()
										.toMono()
										.map(selfLink -> CollectionModel.of(entityModels, selfLink)));
	}

	@GetMapping("/hypermedia/items/{id}")
	public Mono<EntityModel<Item>> findOne(@PathVariable String id) {
		HypermediaItemController controller = methodOn(HypermediaItemController.class);

		Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel().toMono();

		Mono<Link> aggregateLink = linkTo(controller.findAll())
			.withRel(IanaLinkRelations.ITEM).toMono();

		return Mono.zip(itemRepository.findById(id), selfLink, aggregateLink)
			.map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));

	}
}
