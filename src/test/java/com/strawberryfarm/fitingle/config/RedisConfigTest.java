package com.strawberryfarm.fitingle.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisConnectionFactoryNotNull() {
        assertNotNull(redisConnectionFactory);
    }

    @Test
    public void testConnection() {
        String key = "testKey";
        String value = "testValue";

        stringRedisTemplate.opsForValue().set(key, value);
        String retrievedValue = stringRedisTemplate.opsForValue().get(key);

        assertEquals(value, retrievedValue);
    }
    @Test
    public void printAllKeys() {
        Set<String> keys = stringRedisTemplate.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }
        assertNotNull(keys);
    }
}
