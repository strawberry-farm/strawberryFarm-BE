package com.strawberryfarm.fitingle.service;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void insertDummyUsers() {
        for (int i = 1; i <= 10; i++) {
            Users user = Users.builder()
                    .email("dummy" + i + "@example.com")
                    .password("password" + i)
                    .nickname("dummyNick" + i)
                    .interestArea("interestArea" + i)
                    .profileImageUrl("https://example.com/profile" + i + ".jpg")
                    .loginToken("token" + i)
                    .pushToken("pushToken" + i)
                    .aboutMe("About dummy user " + i)
                    .loginType("type" + i)
                    .build();
            userRepository.save(user);
        }
    }
}
