package com.strawberryfarm.fitingle.repository;

import com.strawberryfarm.fitingle.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
