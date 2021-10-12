package io.learn.restservice.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.learn.restservice.controller.ServiceController;
import io.learn.restservice.dto.SubscriptionResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionAssembler implements RepresentationModelAssembler<SubscriptionResponseDto, EntityModel<SubscriptionResponseDto>> {

    @Override
    public EntityModel<SubscriptionResponseDto> toModel(SubscriptionResponseDto entity) {
        return EntityModel.of(
            entity,
            linkTo(methodOn(ServiceController.class).getSubscription(entity.getId())).withSelfRel(),
            linkTo(methodOn(ServiceController.class).getAllSubscriptions()).withRel("subscriptions"),
            linkTo(methodOn(ServiceController.class).deleteSubscription(entity.getId())).withRel("delete")
        );
    }
}
