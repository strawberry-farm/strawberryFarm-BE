package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersAllUsersResponse {
    private List<Users> users;
}
