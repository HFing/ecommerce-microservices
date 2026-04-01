package com.hfing.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hfing.userservice.common.Gender;
import com.hfing.userservice.common.UserStatus;
import lombok.Builder;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record UserProfileDto(
        String id,
        String email,
        String firstName,
        String lastName,
        String avatarKey,

        UserStatus userStatus) {

}
