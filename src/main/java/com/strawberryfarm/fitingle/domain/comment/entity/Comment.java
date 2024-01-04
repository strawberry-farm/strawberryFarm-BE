package com.strawberryfarm.fitingle.domain.comment.entity;

import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qnaId")
    private Qna qna;

    @Lob
    @Column(nullable = false)
    private String contents;

    public void setUser(Users users) {
        this.user = user;
    }


}
