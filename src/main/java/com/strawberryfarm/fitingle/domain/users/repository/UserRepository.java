package com.strawberryfarm.fitingle.domain.users.repository;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findUsersByEmail(String email);
    boolean existsUsersByEmail(String email);
}
