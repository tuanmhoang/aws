package io.learn.restservice.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.stream.Collectors;

import io.learn.restservice.assembler.UserAssembler;
import io.learn.restservice.controller.exception.IdMismatchException;
import io.learn.restservice.dto.UserRequestDto;
import io.learn.restservice.dto.UserResponseDto;
import io.learn.restservice.dto.domain.User;
import io.learn.restservice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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


@RestController
@RequestMapping("/v1/users")
@Api(tags = "User Resource")
public class UserController {

    private final UserService userService;
    private final ConversionService conversionService;
    private final UserAssembler userAssembler;

    @Autowired
    public UserController(UserService userService,
                          ConversionService conversionService,
                          UserAssembler userAssembler) {
        this.userService = userService;
        this.conversionService = conversionService;
        this.userAssembler = userAssembler;
    }

    @PostMapping
    @ApiOperation(value = "Create new user")
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@RequestBody UserRequestDto userRequestDto) {
        var user = userService.addUser(conversionService.convert(userRequestDto, User.class));
        var userResponse = conversionService.convert(user, UserResponseDto.class);

        return ResponseEntity
            .status(CREATED)
            .body(userAssembler.toModel(userResponse));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update user by id")
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(@PathVariable("id") Long id,
                                                                   @RequestBody UserRequestDto userRequestDto) {
        if (!id.equals(userRequestDto.getId())) {
            throw new IdMismatchException(
                String.format("Id in path %s is not equal to id in body %s", id, userRequestDto.getId()));
        }
        var user = conversionService.convert(userRequestDto, User.class);
        var userResponse = conversionService.convert(userService.updateUser(user), UserResponseDto.class);

        return ResponseEntity
            .ok(userAssembler.toModel(userResponse));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);

        return ResponseEntity
            .noContent()
            .build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get user by id")
    public ResponseEntity<EntityModel<UserResponseDto>> getUser(@PathVariable("id") Long id) {
        var userResponse = conversionService.convert(userService.getUser(id), UserResponseDto.class);

        return ResponseEntity
            .ok(userAssembler.toModel(userResponse));
    }

    @GetMapping
    @ApiOperation(value = "Get all users")
    public ResponseEntity<List<EntityModel<UserResponseDto>>> getAllUsers() {
        var users = userService.getAllUser().stream()
            .map(user -> conversionService.convert(user, UserResponseDto.class))
            .map(dto -> userAssembler.toModel(dto))
            .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok(users);
    }
}
