package com.hfing.userservice.controller;

import com.hfing.userservice.dto.request.CreateUserRequest;
import com.hfing.userservice.dto.response.ApiResponse;
import com.hfing.userservice.dto.response.CreateUserResponse;
import com.hfing.userservice.dto.response.UserDetailResponse;
import com.hfing.userservice.dto.response.UserProfileDto;
import com.hfing.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    ApiResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        var data = userService.createUser(request);
        return ApiResponse.<CreateUserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(data)
                .build();
    }

    @GetMapping("/me")
    ApiResponse<UserDetailResponse> getMyInfo(@AuthenticationPrincipal Jwt jwt) {
        var userId = jwt.getSubject();
        var data = userService.myInfo(userId);
        return ApiResponse.<UserDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("User info retrieved successfully")
                .data(data)
                .build();
    }

    @GetMapping
    ApiResponse<List<UserDetailResponse>> getAllUsers() {
        var data = userService.getAllUsers();
        return ApiResponse.<List<UserDetailResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(data)
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserProfileDto> getUserById(@PathVariable String id) {
        var data = userService.getProfileUser(id);
        return ApiResponse.<UserProfileDto>builder()
                .code(HttpStatus.OK.value())
                .message("User info retrieved successfully")
                .data(data)
                .build();
    }

}