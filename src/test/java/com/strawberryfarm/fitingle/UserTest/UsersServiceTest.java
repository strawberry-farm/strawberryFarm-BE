package com.strawberryfarm.fitingle.UserTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordGetResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLogoutResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersPasswordResetRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersSignUpResponseDto;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import com.strawberryfarm.fitingle.domain.users.type.CertificationType;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
	@BeforeAll
	public void setUp() {
		UsersSignUpRequestDto testUsers1 = UsersSignUpRequestDto.builder()
			.email("testUsers1@test.com")
			.nickName("testUsers1")
			.password("123123")
			.build();

		UsersSignUpRequestDto testUsers2 = UsersSignUpRequestDto.builder()
			.email(testUsers)
			.password("123456")
			.nickName("gajamy")
			.build();

		usersService.signUp(testUsers1);
		usersService.signUp(testUsers2);
	}
	@Test
	@DisplayName("Users signUp service test")
	public void usersSignUpServiceTest() {
		//given
		UsersSignUpRequestDto usersSignUpRequestDto = UsersSignUpRequestDto.builder()
			.email("test@naver.com")
			.nickName("testUsers")
			.password("123456")
			.build();

		//when
		ResultDto<?> resultDto = usersService.signUp(usersSignUpRequestDto);

		//then
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getEmail()).isEqualTo(usersSignUpRequestDto.getEmail());
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getNickName()).isEqualTo(usersSignUpRequestDto.getNickName());
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getCreatedDate()).isNotNull();
		assertThat(((UsersSignUpResponseDto)resultDto.getData()).getUpdateDate()).isNotNull();
	}

	@Test
	@DisplayName("Users email-certification service wrong email test")
	public void usersEmailCertificationServiceWrongEmailTest() {
		//given
		EmailCertificationRequestDto requestDto = EmailCertificationRequestDto.builder()
			.email("test@na#@!#er.com")
			.type(CertificationType.SIGNUP)
			.build();

		//when
		ResultDto<?> resultDto = usersService.emailCertification(requestDto);

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.WRONG_EMAIL_FORMAT.getMessage());
		assertThat(resultDto.getData()).isNull();
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.WRONG_EMAIL_FORMAT.getCode());
	}

	@Test
	@DisplayName("Users email-certification service already exist test")
	public void usersEmailCertificationServiceAlreadyExistTest() {
		//given
		EmailCertificationRequestDto requestDto = EmailCertificationRequestDto.builder()
			.email("testUsers1@test.com")
			.type(CertificationType.SIGNUP)
			.build();

		//when
		ResultDto<?> resultDto = usersService.emailCertification(requestDto);
		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.ALREADY_EXIST_USERS.getMessage());
		assertThat(resultDto.getData()).isNull();
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXIST_USERS.getCode());
	}

	@Test
	@DisplayName("Users email-certification service success test")
	public void usersEmailCertificationServiceSuccessTest() {
		//given
		EmailCertificationRequestDto requestDto = EmailCertificationRequestDto.builder()
			.email("testUsers@test.com")
			.type(CertificationType.SIGNUP)
			.build();

		//when
		ResultDto<?> resultDto = usersService.emailCertification(requestDto);
		String val = usersService.getData(requestDto.getEmail());

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(resultDto.getData()).isNotNull();
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
		assertThat(val).isNotNull();
	}

	@Test
	@DisplayName("Users email-certification confirm wrong email test")
	public void usersEmailCertificationConfirmWrongEmailTest() {
		//given

		EmailCertificationConfirmRequestDto requestDto = EmailCertificationConfirmRequestDto.builder()
			.email("test@na#@!#er.com")
			.code("1111111")
			.type(CertificationType.SIGNUP)
			.build();

		//when
		ResultDto<?> resultDto = usersService.emailCertificationConfirm(requestDto);

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.WRONG_EMAIL_FORMAT.getMessage());
		assertThat(resultDto.getData()).isNull();
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.WRONG_EMAIL_FORMAT.getCode());
	}

	@Test
	@DisplayName("Users email-certification confirm fail certification test")
	public void usersEmailAuthConfirmWrongCertificationTest() {
		//given
		usersService.DummyInputRedisCertification(testUsers,123123,30);
		String certificationCode = "asdfasdf123";

		EmailCertificationConfirmRequestDto requestDto = EmailCertificationConfirmRequestDto.builder()
			.email(testUsers)
			.code(certificationCode)
			.type(CertificationType.SIGNUP)
			.build();

		//when
		ResultDto<?> resultDto = usersService.emailCertificationConfirm(requestDto);

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.FAIL_CERTIFICATION.getMessage());
		assertThat(resultDto.getData()).isNull();
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.FAIL_CERTIFICATION.getCode());
	}

	@Test
	@DisplayName("Users email-certification confirm success test")
	public void usersEmailCertificationConfirmCertificationSuccessTest() {
		//given
		usersService.DummyInputRedisCertification("Dummy@Dummy.com",123123,30);

		String certificationCode = usersService.getData("Dummy@Dummy.com");

		EmailCertificationConfirmRequestDto requestDto = EmailCertificationConfirmRequestDto.builder()
			.email("Dummy@Dummy.com")
			.code(certificationCode)
			.type(CertificationType.SIGNUP)
			.build();

		//when
		ResultDto<?> resultDto = usersService.emailCertificationConfirm(requestDto);

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(resultDto.getData()).isNotNull();
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
		assertThat(((EmailCertificationConfirmResponseDto)resultDto.getData()).getEmail()).isEqualTo("Dummy@Dummy.com");
	}

	@Test
	@DisplayName("Users logout success test")
	public void usersLogoutSuccessTest(){
		//given
		UsersLoginRequestDto loginRequestDto = UsersLoginRequestDto.builder()
			.email("testUsers1@test.com")
			.password("123123")
			.build();

		ResultDto<?> loginResult = usersService.login(loginRequestDto);
		String refreshToken = ((UsersLoginResponseVo)loginResult.getData()).getRefreshToken();

		//when
		ResultDto<?> resultDto = usersService.logout(refreshToken);

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(((UsersLogoutResponseDto)resultDto.getData()).getEmail()).isEqualTo("testUsers1@test.com");
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}

	@Test
	@DisplayName("Users detail not exist test")
	public void usersDetailNotExistTest() {
		//given
		Long userId = 123123L;

		//when
		ResultDto<?> resultDto = usersService.getUsersDetail(userId);

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.NOT_EXIST_USERS.getMessage());
		assertThat(resultDto.getData()).isNull();
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_USERS.getCode());
	}

	@Test
	@DisplayName("Users detail success test")
	public void usersDetailSuccessTest() {
		//given
		List<Users> users = usersService.getUsersList().getUsers();

		Long userId = users.get(0).getId();
		String email = users.get(0).getEmail();
		String aboutMe = users.get(0).getAboutMe();
		String nickname = users.get(0).getNickname();
		String profileUrl = users.get(0).getProfileImageUrl();

		//when
		ResultDto<?> resultDto = usersService.getUsersDetail(userId);

		//then
		UsersDetailResponseDto usersDetailResponseDto = (UsersDetailResponseDto) resultDto.getData();
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(usersDetailResponseDto.getEmail()).isEqualTo(email);
		assertThat(usersDetailResponseDto.getAboutMe()).isEqualTo(aboutMe);
		assertThat(usersDetailResponseDto.getNickname()).isEqualTo(nickname);
		assertThat(usersDetailResponseDto.getProfileUrl()).isEqualTo(profileUrl);
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}

	@Test
	@DisplayName("Users update success test")
	public void usersPatchSuccessTest() {
		//given
		List<Users> users = usersService.getUsersList().getUsers();

		Long userId = users.get(0).getId();
		String email = users.get(0).getEmail();
		String aboutMe = "test About Me";
		String nickname = "test nickname";
		String profileUrl = "testUrl";

		UsersDetailUpdateRequestDto requestDto = UsersDetailUpdateRequestDto.builder()
			.aboutMe(aboutMe)
			.nickname(nickname)
			.profileUrl(profileUrl)
			.build();

		//when
		ResultDto<?> resultDto = usersService.updateUsersDetail(userId, requestDto);

		//then
		UsersDetailUpdateResponseDto responseDto = (UsersDetailUpdateResponseDto)resultDto.getData();
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(responseDto.getUserId()).isEqualTo(userId);
		assertThat(responseDto.getEmail()).isEqualTo(email);
		assertThat(responseDto.getAboutMe()).isEqualTo(aboutMe);
		assertThat(responseDto.getNickname()).isEqualTo(nickname);
		assertThat(responseDto.getProfileUrl()).isEqualTo(profileUrl);
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}

	@Test
	@DisplayName("Users password Reset success test")
	public void usersPasswordResetSuccessTest() {
		//given
		List<Users> users = usersService.getUsersList().getUsers();

		String beforePassword = "";
		for (Users u : users) {
			if (u.getEmail().equals("mansa0805@naver.com")) {
				beforePassword = u.getPassword();
				break;
			}
		}
		UsersPasswordResetRequestDto usersPasswordResetRequestDto = UsersPasswordResetRequestDto.builder()
			.email("mansa0805@naver.com")
			.password("7891011")
			.build();


		//when
		ResultDto<?> resultDto = usersService.passwordEdit(usersPasswordResetRequestDto);


		//then

		List<Users> users2 = usersService.getUsersList().getUsers();

		String afterPassword = "";

		for (Users u : users2) {
			if (u.getEmail().equals("mansa0805@naver.com")) {
				afterPassword = u.getPassword();
				break;
			}
		}
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(resultDto.getData()).isNotNull();
		assertThat(beforePassword).isNotEqualTo(afterPassword);
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}
	@Test
	@DisplayName("Users get InterestArea Success Test")
	public void usersRegisterInterestAreaSuccessTest() {
		//given
		List<Users> users = usersService.getUsersList().getUsers();

		Long userId = users.get(0).getId();

		InterestAreaRegisterRequestDto requestDto = InterestAreaRegisterRequestDto.builder()
			.b_code("11305")
			.build();

		usersService.registerInterestArea(userId,requestDto);
		//when
		ResultDto<?> resultDto = usersService.getInterestArea(userId);
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(((InterestAreaResponseDto)resultDto.getData()).getB_code()).isEqualTo("11305");
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}
	@Test
	@DisplayName("Users get InterestArea Success Test")
	public void usersGetInterestAreaSuccessTest() {
		//given
		List<Users> users = usersService.getUsersList().getUsers();

		Long userId = users.get(0).getId();

		InterestAreaRegisterRequestDto requestDto = InterestAreaRegisterRequestDto.builder()
			.b_code("11305")
			.build();

		usersService.registerInterestArea(userId,requestDto);
		//when
		ResultDto<?> resultDto = usersService.getInterestArea(userId);
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(((InterestAreaResponseDto)resultDto.getData()).getB_code()).isEqualTo("11305");
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}

	@Test
	@DisplayName("Keyword register SuccessTest")
	public void keywordRegisterSuccessTest() {
		//given
		Long userId = 1L;

		KeywordRegisterRequestDto requestDto3 = KeywordRegisterRequestDto.builder()
			.keyword("세번째 키워드")
			.build();

		//when
		ResultDto<?> resultDto = usersService.registerKeyword(userId, requestDto3);

		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(((KeywordRegisterResponseDto)resultDto.getData()).getKeywords().size()).isEqualTo(1);
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}

	@Test
	@DisplayName("Keyword register SuccessTest")
	public void keywordGetSuccessTest() {
		//given
		Long userId = 1L;

		KeywordRegisterRequestDto requestDto1 = KeywordRegisterRequestDto.builder()
			.keyword("세번째 키워드")
			.build();

		ResultDto<?> resultDto1 = usersService.registerKeyword(userId, requestDto1);

		KeywordRegisterRequestDto requestDto2 = KeywordRegisterRequestDto.builder()
			.keyword("세번째 키워드")
			.build();

		ResultDto<?> resultDto2 = usersService.registerKeyword(userId, requestDto2);
		//when

		ResultDto<?> resultDto = usersService.getKeyword(1L);

		usersService.deleteKeyword(1L,1L);

		usersService.getKeyword(1L);

		//then
		assertThat(resultDto.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
		assertThat(((KeywordGetResponseDto)resultDto.getData()).getKeywords().size()).isEqualTo(2);
		assertThat(resultDto.getErrorCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
	}

}
