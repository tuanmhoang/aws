package io.learn.restservice.service;

import java.util.List;

import io.learn.restservice.dto.domain.User;

public interface UserService {

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    User getUser(Long id);

    List<User> getAllUser();
}
