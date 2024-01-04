package com.strawberryfarm.fitingle.domain.chat.repository;

import com.strawberryfarm.fitingle.domain.chat.entity.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	@Query("select c from Chat c where c.chatIdx >= :start")
	List<Chat> findChats(@Param("start") Long start);
}
