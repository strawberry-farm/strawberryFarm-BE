package com.strawberryfarm.fitingle.security.authservice;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UserRepository;
import com.strawberryfarm.fitingle.security.authobject.AuthUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUsers authUsers = new AuthUsers(userRepository.findUsersByEmail(username).orElseThrow(() -> new UsernameNotFoundException("사용자 없음")));
        return new User(authUsers.getUsername(),authUsers.getPassword(),authUsers.getAuthorities());
    }
}
