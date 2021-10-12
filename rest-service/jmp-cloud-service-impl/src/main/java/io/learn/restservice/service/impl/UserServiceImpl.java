package io.learn.restservice.service.impl;

import java.util.List;

import io.learn.restservice.dto.domain.User;
import io.learn.restservice.exception.UserNotFoundException;
import io.learn.restservice.repository.UserRepository;
import io.learn.restservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return userRepository.save(user);
        } else {
            throw new UserNotFoundException(String.format("User with id '%s' is not found", user.getId()));
        }

    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(String.format("User with id '%s' is not found", id)));
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
