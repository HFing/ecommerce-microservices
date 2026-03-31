package com.hfing.chatservice.repository.httpclient;

import com.hfing.chatservice.dto.response.ApiResponse;
import com.hfing.chatservice.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface  ProfileClient {
    @GetMapping("/api/v1/users/{id}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable("id") String id);
}
