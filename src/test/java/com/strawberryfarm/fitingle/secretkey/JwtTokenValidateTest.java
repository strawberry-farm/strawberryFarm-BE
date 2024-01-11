package com.strawberryfarm.fitingle.secretkey;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Transactional
public class JwtTokenValidateTest {
	@Autowired
	private JwtTokenManager jwtTokenManager;

    @Test
    @DisplayName("토큰 검증 테스트 OK")
    public void validateTokenOk() throws InterruptedException {
        Long expireTime = 5L;
        String compact = jwtTokenManager.genToken(expireTime);
        ResultDto resultDto = new ResultDto();

        TimeUnit.SECONDS.sleep(expireTime - 2);
        boolean result = jwtTokenManager.accessTokenValidate(compact,resultDto);
        assertEquals(true,result);
    }

    @Test
    @DisplayName("토큰 검증 테스트 시간 만료")
    public void validateTokenExpired() throws InterruptedException {
        Long expireTime = 5L;
        String compact = jwtTokenManager.genToken(expireTime);
        ResultDto resultDto = new ResultDto();

        TimeUnit.SECONDS.sleep(expireTime + 1);
        assertEquals(false,jwtTokenManager.accessTokenValidate(compact,resultDto));
        assertEquals("0101",resultDto.getErrorCode());
    }
}
