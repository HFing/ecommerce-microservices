package com.hfing.userservice.controller;

import com.hfing.userservice.dto.request.LoginRequest;
import com.hfing.userservice.dto.response.ApiResponse;
import com.hfing.userservice.dto.response.LoginResponse;
import com.hfing.userservice.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request,  HttpServletResponse response) {
        var data = authenticationService.login(request);

        Cookie cookie = new Cookie("refresh_token", data.refreshToken());
        cookie.setHttpOnly(true); // Prevents JavaScript from accessing the cookie (XSS protection)
        cookie.setSecure(false); // Change to true in production
        cookie.setPath("/"); // Cookie is accessible across all paths in the app
        cookie.setMaxAge(14 * 24 * 60 * 60); // Cookie expiry: 14 days — matches refresh token TTL
        response.addCookie(cookie);

        LoginResponse responseData = LoginResponse.builder()
                .accessToken(data.accessToken())
                .refreshToken(null)
                .roles(data.roles())
                .build();

        return ApiResponse.<LoginResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .data(responseData)
                .build();
    }

    @PostMapping("/refresh-token")
    ApiResponse<LoginResponse> refreshToken(@CookieValue("refresh_token") String refreshToken) {
        var data = authenticationService.refreshToken(refreshToken);

        return ApiResponse.<LoginResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Token refreshed successfully")
                .data(data)
                .build();
    }
}
