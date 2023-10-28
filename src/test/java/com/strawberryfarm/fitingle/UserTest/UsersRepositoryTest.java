package com.strawberryfarm.fitingle.UserTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Users Repository Test")
public class UsersRepositoryTest {
	@Autowired
	private UsersRepository usersRepository;

	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	@BeforeAll
	public void setUp(){
		Users testUser1 = Users.builder()
			.email("test1@naver.com")
			.password(passwordEncoder.encode("123456"))
			.nickname("testUser1")
			.roles("ROLE_USERS")
			.profileImageUrl("default")
			.signUpType(SignUpType.FITINGLE)
			.status(UsersStatus.AUTHORIZED)
			.createdDate(LocalDateTime.now())
			.updateDate(LocalDateTime.now())
			.build();

		Users testUsers2 = Users.builder()
			.email("test2@naver.com")
			.password(passwordEncoder.encode("456789"))
			.nickname("testUser2")
			.roles("ROLE_USERS")
			.profileImageUrl("default")
			.signUpType(SignUpType.FITINGLE)
			.status(UsersStatus.AUTHORIZED)
			.createdDate(LocalDateTime.now())
			.updateDate(LocalDateTime.now())
			.build();

		usersRepository.save(testUser1);
		usersRepository.save(testUsers2);

	}

	@Test
	@DisplayName("Users Save Repository Test")
	public void saveUsersTest() {
		//given
		Users newUsers = Users.builder()
			.email("test3@naver.com")
			.password(passwordEncoder.encode("890674"))
			.nickname("testUser3")
			.roles("ROLE_USERS")
			.profileImageUrl("default")
			.signUpType(SignUpType.FITINGLE)
			.status(UsersStatus.AUTHORIZED)
			.createdDate(LocalDateTime.now())
			.updateDate(LocalDateTime.now())
			.build();
		//when
		Users savedUsers = usersRepository.save(newUsers);

		//then
		assertThat(savedUsers).isSameAs(newUsers);
		assertThat(savedUsers.getEmail()).isEqualTo(newUsers.getEmail());
		assertThat(savedUsers.getNickname()).isEqualTo(newUsers.getNickname());
		assertThat(savedUsers.getSignUpType()).isEqualTo(newUsers.getSignUpType());
	}

	@Test
	@DisplayName("Users Get Repository Test")
	public void getUsersTest() {
		//given

		//when
		Users findUsers = usersRepository.findUsersByEmail("test1@naver.com").get();

		//then
		assertThat(findUsers.getNickname()).isEqualTo("testUser1");
		assertThat(findUsers.getSignUpType()).isEqualTo(SignUpType.FITINGLE);
	}

	@Test
	@DisplayName("Users Patch Repository Test")
	public void updateUsersTest() {
		//given
		Users findUsers = usersRepository.findUsersByEmail("test2@naver.com").get();

		//when
		findUsers.modifyNickname("testUsers2Changed");
		Users patchedUsers = usersRepository.save(findUsers);

		//then
		assertThat(patchedUsers.getNickname()).isEqualTo(findUsers.getNickname());
	}
}
