package com.strawberryfarm.fitingle.domain.users.repository;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findUsersByEmail(String email);
    boolean existsUsersByEmail(String email);

    Optional<Users> findUsersById(Long userId);
    Optional<Users> findById(Long userId);

}
