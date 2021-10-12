package io.learn.restservice.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.learn.restservice.controller.UserController;
import io.learn.restservice.dto.UserResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class UserAssembler implements RepresentationModelAssembler<UserResponseDto, EntityModel<UserResponseDto>> {

    @Override
    public EntityModel<UserResponseDto> toModel(UserResponseDto entity) {
        return EntityModel.of(
            entity,
            linkTo(methodOn(UserController.class).getUser(entity.getId())).withSelfRel(),
            linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
            linkTo(methodOn(UserController.class).deleteUser(entity.getId())).withRel("delete")
        );
    }
}
