package com.hfing.userservice.service;

import com.hfing.userservice.dto.request.CreateUserRequest;
import com.hfing.userservice.dto.response.CreateUserResponse;
import com.hfing.userservice.dto.response.UserDetailResponse;
import com.hfing.userservice.dto.response.UserProfileDto;

import java.util.List;


public interface UserService {
    CreateUserResponse createUser(CreateUserRequest request);
    UserDetailResponse myInfo(String userId);
    List<UserDetailResponse> getAllUsers();
    UserProfileDto getProfileUser(String userId);
}