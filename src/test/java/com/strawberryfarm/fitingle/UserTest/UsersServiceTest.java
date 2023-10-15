package com.strawberryfarm.fitingle.UserTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersSignUpResponseDto;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Users Service Test")
public class UsersServiceTest {
	@Mock
	private UsersRepository usersRepository;

	@InjectMocks
	private UsersService usersService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("Users signUp service test")
	public void usersSignUpServiceTest() {
		//given
		UsersSignUpRequestDto usersSignUpRequestDto = UsersSignUpRequestDto.builder()
			.email("test@naver.com")
			.nickName("testUsers")
			.password("123456")
			.build();
		UsersSignUpResponseDto usersSignUpResponseDto = UsersSignUpResponseDto.builder()
			.email(usersSignUpRequestDto.getEmail())
			.nickName(usersSignUpRequestDto.getNickName())
			.createdDate(LocalDateTime.now())
			.updateDate(LocalDateTime.now())
			.build();

		//when
		ResultDto resultDto = usersService.signUp(usersSignUpRequestDto);

		//then
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getEmail()).isEqualTo(usersSignUpRequestDto.getEmail());
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getNickName()).isEqualTo(usersSignUpRequestDto.getNickName());
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getCreatedDate()).isNotNull();
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getUpdateDate()).isNotNull();
	}
}
