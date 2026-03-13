package com.hfing.userservice.service;

import com.hfing.userservice.dto.request.LoginRequest;
import com.hfing.userservice.dto.response.LoginResponse;

public interface AuthenticationService {
    LoginResponse login(LoginRequest request);
}
