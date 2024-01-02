package com.strawberryfarm.fitingle.domain.userchatroom.entity;

import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.chatRoom.entity.ChatRoom;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "chat_chatroom")
@AllArgsConstructor
@NoArgsConstructor
public class UsersChatRoom extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "USER_ID")
	private Users users;

	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "CHATROOM_ID")
	private ChatRoom chatRoom;

	public void modifyUsers(Users users) {
		this.users = users;
		users.getUsersChatRooms().add(this);
	}

	public void modifyChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
		chatRoom.getUsersChatRooms().add(this);
	}
}
