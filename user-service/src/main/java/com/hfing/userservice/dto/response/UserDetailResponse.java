package com.hfing.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hfing.userservice.common.Gender;
import com.hfing.userservice.common.UserStatus;
import lombok.Builder;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import java.time.LocalDate;


@Builder
@JsonInclude(NON_NULL)
public record UserDetailResponse(
        String email,
        String firstName,
        String lastName,
        String phone,
        String avatarKey,
        Gender gender,
        LocalDate birthDate,
        UserStatus userStatus
) {
}
