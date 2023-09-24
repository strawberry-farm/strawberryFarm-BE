package com.strawberryfarm.fitingle.domain;

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
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "groups")
public class Groups extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;

    @Enumerated(EnumType.STRING)
    private GroupsStatus status;

    // 연관관계 메서드
    public void setUser(Users user) {
        this.user = user;
        user.getGroups().add(this);
    }
    public void setBoard(Board board) {
        this.board = board;
        board.getGroups().add(this);
    }


}
