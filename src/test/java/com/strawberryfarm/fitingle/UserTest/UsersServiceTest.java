package com.strawberryfarm.fitingle.UserTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Users Service Test")
public class UsersServiceTest {
	@Autowired
	private UsersService usersService;

	@Value("${testMail.username}")
	private String testUsers;
}
