package com.hfing.userservice.mapper;

import com.hfing.userservice.dto.request.CreateUserRequest;
import com.hfing.userservice.dto.response.CreateUserResponse;
import com.hfing.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toUser(CreateUserRequest request);

    CreateUserResponse toCreateUserResponse(User user);
}