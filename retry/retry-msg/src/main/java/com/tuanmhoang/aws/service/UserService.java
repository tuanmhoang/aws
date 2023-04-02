package com.tuanmhoang.aws.service;

import com.tuanmhoang.aws.model.User;

public interface UserService {
    User fetchData(String id);

    void createDummyData();
}
