package com.strawberryfarm.fitingle.domain.groups.entity;

import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "my_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Groups extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY) // 여러 Groups가 하나의 Board에 매핑될 수 있음
    @JoinColumn(name = "boardId")
    private Board board;

    @Enumerated(EnumType.STRING)
    private GroupsStatus status;


    @Builder
    public Groups(Users user, Board board, GroupsStatus status){
        this.user = user;
        this.board = board;
        this.status = status;
    }

    // 연관관계 메서드
    public void setUser(Users user) {
        this.user = user;
        if (!user.getGroups().contains(this)) {
            user.getGroups().add(this);
        }
    }

    public void setBoard(Board board) {
        this.board = board;
        if (!board.getGroups().contains(this)) {
            board.getGroups().add(this);
        }
    }
}
