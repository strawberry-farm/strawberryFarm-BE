package com.strawberryfarm.fitingle.domain.users.repository;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
