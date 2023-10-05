package com.strawberryfarm.fitingle.domain.chat.repository;

import com.strawberryfarm.fitingle.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
