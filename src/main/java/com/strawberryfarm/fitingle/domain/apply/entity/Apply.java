package com.strawberryfarm.fitingle.domain.apply.entity;

import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Table(name = "apply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Apply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId")
    private Board board;

    @Lob
    private String contents;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

    public void setUser(Users users) {
        this.user = users;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void modifyStatus(ApplyStatus status) {
        this.status = status;
    }

}


