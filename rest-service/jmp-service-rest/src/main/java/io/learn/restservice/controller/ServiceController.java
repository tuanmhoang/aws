package io.learn.restservice.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.learn.restservice.assembler.SubscriptionAssembler;
import io.learn.restservice.controller.exception.IdMismatchException;
import io.learn.restservice.dto.SubscriptionRequestDto;
import io.learn.restservice.dto.SubscriptionResponseDto;
import io.learn.restservice.dto.domain.Subscription;
import io.learn.restservice.service.SubscriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/subscriptions")
@Api(tags = "Subscription Resource")
public class ServiceController {

	private final SubscriptionService subscriptionService;
	private final ConversionService conversionService;
	private final SubscriptionAssembler subscriptionAssembler;

	public ServiceController(SubscriptionService subscriptionService, ConversionService conversionService,
			SubscriptionAssembler subscriptionAssembler) {
		this.subscriptionService = subscriptionService;
		this.conversionService = conversionService;
		this.subscriptionAssembler = subscriptionAssembler;
	}

	@PostMapping
	@ApiOperation(value = "Create new subscription")
	public ResponseEntity<EntityModel<SubscriptionResponseDto>> createSubscription(
			@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
		var subscription = subscriptionService
				.createSubscription(conversionService.convert(subscriptionRequestDto, Subscription.class));
		var subscriptionResponse = conversionService.convert(subscription, SubscriptionResponseDto.class);

		return ResponseEntity.status(CREATED).body(subscriptionAssembler.toModel(subscriptionResponse));
	}

	@PutMapping("/{id}")
	@ApiOperation(value = "Update subscription by id")
	public ResponseEntity<EntityModel<SubscriptionResponseDto>> updateSubscription(@PathVariable("id") Long id,
			@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
		if (!id.equals(subscriptionRequestDto.getId())) {
			throw new IdMismatchException(
					String.format("Id in path %s is not equal to id in body %s", id, subscriptionRequestDto.getId()));
		}
		var subscription = subscriptionService
				.updateSubscription(conversionService.convert(subscriptionRequestDto, Subscription.class));
		var subscriptionResponse = conversionService.convert(subscription, SubscriptionResponseDto.class);

		return ResponseEntity.ok(subscriptionAssembler.toModel(subscriptionResponse));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "Delete subscription by id")
	public ResponseEntity<Void> deleteSubscription(@PathVariable("id") Long id) {
		subscriptionService.deleteSubscription(id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "Get subscription by id")
	public ResponseEntity<EntityModel<SubscriptionResponseDto>> getSubscription(@PathVariable("id") Long id) {
		var subscriptionResponse = conversionService.convert(subscriptionService.getSubscription(id),
				SubscriptionResponseDto.class);

		return ResponseEntity.ok(subscriptionAssembler.toModel(subscriptionResponse));
	}

	@GetMapping
	@ApiOperation(value = "Get all subscriptions")
	public ResponseEntity<List<EntityModel<SubscriptionResponseDto>>> getAllSubscriptions() {
		var subscriptions = subscriptionService.getAllSubscriptions().stream()
				.map(subscription -> conversionService.convert(subscription, SubscriptionResponseDto.class))
				.map(dto -> subscriptionAssembler.toModel(dto)).collect(Collectors.toUnmodifiableList());
		return ResponseEntity.ok(subscriptions);
	}
}
