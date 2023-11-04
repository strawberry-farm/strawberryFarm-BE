package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersAllUsersResponse {
    private List<Users> users;
}
