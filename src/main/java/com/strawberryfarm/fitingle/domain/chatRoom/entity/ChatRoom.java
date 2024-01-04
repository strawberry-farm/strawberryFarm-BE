package com.strawberryfarm.fitingle.domain.chatRoom.entity;

import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.chat.entity.Chat;
import com.strawberryfarm.fitingle.domain.userchatroom.entity.UsersChatRoom;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "chatroom")
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int numsOfUsers;

	private String chatRoomSummaryInfo;

	@Builder.Default
	@OneToMany(mappedBy = "chatRoom",fetch = FetchType.LAZY)
	private List<UsersChatRoom> usersChatRooms = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "chatRoom",fetch = FetchType.LAZY)
	private List<Chat> chats = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOARD_ID")
	private Board board;

	public void reduceNumOfUsers() {
		this.numsOfUsers--;
	}

	public void modifyChatRoomSummaryInfo(String chatRoomSummaryInfo) {
		this.chatRoomSummaryInfo = chatRoomSummaryInfo;
	}
}
