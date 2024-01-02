package com.strawberryfarm.fitingle.domain.chatRoom.repository;

import com.strawberryfarm.fitingle.domain.chatRoom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
