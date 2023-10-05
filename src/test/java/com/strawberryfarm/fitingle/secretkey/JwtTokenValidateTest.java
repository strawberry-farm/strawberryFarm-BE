package com.strawberryfarm.fitingle.secretkey;

import static org.junit.Assert.assertEquals;

import com.strawberryfarm.fitingle.security.JwtTokenManager;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTokenValidateTest {
	@Autowired
	private JwtTokenManager jwtTokenManager;

    @Test
    @DisplayName("토큰 검증 테스트 OK")
    public void validateTokenOk() throws InterruptedException {
        Long expireTime = 5L;
        String compact = jwtTokenManager.genToken(expireTime);

        TimeUnit.SECONDS.sleep(expireTime - 2);
        assertEquals(true,jwtTokenManager.accessTokenValidate(compact));
    }

    @Test
    @DisplayName("토큰 검증 테스트 시간 만료")
    public void validateTokenExpired() throws InterruptedException {
        Long expireTime = 5L;
        String compact = jwtTokenManager.genToken(expireTime);

        TimeUnit.SECONDS.sleep(expireTime + 1);
        assertEquals(false,jwtTokenManager.accessTokenValidate(compact));
    }
}
