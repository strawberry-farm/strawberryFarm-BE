package com.strawberryfarm.fitingle.UserTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UsersService usersService;

    @BeforeAll
    private void SetUp() {
        UsersSignUpRequestDto testData1 = UsersSignUpRequestDto.builder()
            .email("test1@naver.com")
            .password("123123")
            .nickName("PowerMan1")
            .build();

        UsersSignUpRequestDto testData2 = UsersSignUpRequestDto.builder()
            .email("test2@naver.com")
            .password("123123")
            .nickName("PowerMan2")
            .build();

        usersService.signUp(testData1);
        usersService.signUp(testData2);
    }

    //회원 가입 테스트
    @Test
    @DisplayName("UserController SignUp Test")
    public void SignUpTest() throws Exception {
        //given
        UsersSignUpRequestDto usersSignUpRequestDto = UsersSignUpRequestDto.builder()
            .email("test@naver.com")
            .password("123123")
            .nickName("PowerMan")
            .build();

        //when
        String json = new Gson().toJson(usersSignUpRequestDto);

        //then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value("success"))
            .andExpect(jsonPath("$.data.email").value("test@naver.com"))
            .andExpect(jsonPath("$.data.nickName").value("PowerMan"))
            .andExpect(jsonPath("$.data.createdDate").exists())
            .andExpect(jsonPath("$.data.updateDate").exists())
            .andExpect(jsonPath("errorCode").value("1111"))
            .andDo(print());
    }

    //이메일 인증 관련 테스트 모음
    @Test
    @DisplayName("UserController Email Auth CompleteTest")
    public void emailAuthCompleteTest() throws Exception {
        //given
        EmailCertificationRequestDto emailCertificationRequestDto = EmailCertificationRequestDto.builder()
            .email("mansa0805@naver.com")
            .build();

        //when
        String json = new Gson().toJson(emailCertificationRequestDto);

        //then
        mockMvc.perform(post("/auth/email-request")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
            .andExpect(jsonPath("message").value("success"))
            .andExpect(jsonPath("$.data.email").value("mansa0805@naver.com"))
            .andExpect(jsonPath("errorCode").value("1111"))
            .andDo(print());
    }

    @Test
    @DisplayName("UserController Email Auth Duplicate Email")
    public void emailAuthDuplicateTest() throws Exception {
        //given
        EmailCertificationRequestDto emailCertificationRequestDto = EmailCertificationRequestDto.builder()
            .email("test1@naver.com")
            .build();

        //when
        String json = new Gson().toJson(emailCertificationRequestDto);

        //then
        mockMvc.perform(post("/auth/email-request")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value("already exist email"))
            .andExpect(jsonPath("data").isEmpty())
            .andExpect(jsonPath("errorCode").value("0001"))
            .andDo(print());
    }

    @Test
    @DisplayName("UserController Email Auth Incorrect Code")
    public void emailAuthIncorrectCodeTest() throws Exception {
        //given
        EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto =
            EmailCertificationConfirmRequestDto.builder()
                .email("test3@naver.com")
                .code("222222")
                .build();

        //when
        String json = new Gson().toJson(emailCertificationConfirmRequestDto);
        usersService.DummyInputRedisCertification("test3@naver.com",123123,300);

        //then
        mockMvc.perform(post("/auth/email-confirm")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value("Fail Certificate"))
            .andExpect(jsonPath("data").isEmpty())
            .andExpect(jsonPath("errorCode").value("0004"))
            .andDo(print());
    }

    // 로그인 관련 테스트 모음
    @Test
    @DisplayName("UsersController Login Test")
    public void LoginTest() throws Exception {
        //given
        UsersLoginRequestDto usersLoginRequestDto = UsersLoginRequestDto.builder()
            .email("test1@naver.com")
            .password("123123")
            .build();

        //when
        String json = new Gson().toJson(usersLoginRequestDto);

        //then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("message").value("success"))
            .andExpect(jsonPath("$.data.userId").exists())
            .andExpect(jsonPath("$.data.email").value("test1@naver.com"))
            .andExpect(jsonPath("$.data.nickName").value("PowerMan1"))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("errorCode").value("1111"))
            .andExpect(cookie().exists("refreshToken"))
            .andDo(print());
    }

    //비밀 번호 재설정 테스트
    @Test
    @DisplayName("Auth Error Test")
    public void AuthErrorTest() throws Exception {
        //given


        //when

        //then
//        mockMvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(json))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("message").value("success"))
//            .andDo(print());
    }

    //비밀 번호 재설정 테스트
    @Test
    @DisplayName("UsersController PasswordReset Complete Test")
    public void PasswordResetCompleteTest() throws Exception {
        //given


        //when

        //then
//        mockMvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(json))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("message").value("success"))
//            .andDo(print());
    }

    @Test
    @DisplayName("UsersController PasswordReset Invalid Format Test")
    public void PasswordResetInvalidFormatTest() throws Exception {
        //given

        //when

        //then
//        mockMvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(json))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("message").value("success"))
//            .andDo(print());
    }

    @Test
    @DisplayName("UsersController PasswordReset Email Auth Incorrect Code Test")
    public void PasswordReset() throws Exception {
        //given

        //when

        //then
//        mockMvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(json))
//            .andExpect(status().isOk())
//            .andDo(print());
    }

    //부가적인 API 테스트
    @Test
    @DisplayName("Users List TEST")
    public void getUsersList() throws Exception {
        mockMvc.perform(get("/auth/list")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andDo(print());
    }
}