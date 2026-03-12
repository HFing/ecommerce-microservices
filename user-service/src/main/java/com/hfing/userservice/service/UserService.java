package com.hfing.userservice.service;

import com.hfing.userservice.dto.request.CreateUserRequest;
import com.hfing.userservice.dto.response.CreateUserResponse;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
}