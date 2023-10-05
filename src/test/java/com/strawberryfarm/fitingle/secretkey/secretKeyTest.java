package com.strawberryfarm.fitingle.secretkey;

import com.strawberryfarm.fitingle.security.JwtTokenManager;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class secretKeyTest {
    private String secretKeyPlain = "strawberrySecretKeyBerryBerry1315@@";

    @Test
    @DisplayName("Make Base64 Encoded SecretKey")
    public void getEncodeKey() {
        String encodeKey = Encoders.BASE64.encode(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
        System.out.println(encodeKey);
    }
    @Test
    @DisplayName("시크릿 키 작동확인")
    public void genSecretKey() {
        String encryptKey = Encoders.BASE64.encode(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
        String decodeKey = new String(Decoders.BASE64.decode(encryptKey));
        System.out.println("decodeKey = " + encryptKey);
        assertEquals(secretKeyPlain, decodeKey);
    }

}
