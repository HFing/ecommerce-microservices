package com.hfing.userservice.service.impl;

import com.hfing.userservice.common.RoleType;
import com.hfing.userservice.dto.request.CreateUserRequest;
import com.hfing.userservice.dto.response.CreateUserResponse;
import com.hfing.userservice.entity.Role;
import com.hfing.userservice.entity.User;
import com.hfing.userservice.exception.ErrorCode;
import com.hfing.userservice.exception.UserServiceException;
import com.hfing.userservice.mapper.UserMapper;
import com.hfing.userservice.repository.UserRepository;
import com.hfing.userservice.service.RoleService;
import com.hfing.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        Role role = roleService.createRole(RoleType.CUSTOMER.name());
        user.addRole(role);
        try {
            userRepository.save(user);
        }catch (DataIntegrityViolationException exception) {
            log.error("User already exists");
            throw new UserServiceException(ErrorCode.USER_ALREADY_EXISTS);
        }
        return userMapper.toCreateUserResponse(user);
    }


}