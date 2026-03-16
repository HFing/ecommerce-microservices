package com.hfing.userservice.service;

import com.hfing.userservice.dto.request.CreateUserRequest;
import com.hfing.userservice.dto.response.CreateUserResponse;
import com.hfing.userservice.dto.response.UserDetailResponse;


public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
    UserDetailResponse myInfo(String userId);
}