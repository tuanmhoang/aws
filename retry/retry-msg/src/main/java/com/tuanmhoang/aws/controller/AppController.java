package com.tuanmhoang.aws.controller;

import com.tuanmhoang.aws.model.User;
import com.tuanmhoang.aws.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AppController {

    private final UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getData(@PathVariable String id) {
        return ResponseEntity.ok(userService.fetchData(id));
    }

    @PostMapping("/user/dummy")
    public void createDummyData() {
        userService.createDummyData();
    }
}
