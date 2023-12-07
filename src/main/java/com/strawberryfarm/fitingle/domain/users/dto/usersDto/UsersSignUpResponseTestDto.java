package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//Gson이 LocalDateTime을 못알아 처먹어서 String으로 해보기 위해
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersSignUpResponseTestDto {
    private String email;
    private String nickName;
    private String createdDate;
    private String updateDate;
}
