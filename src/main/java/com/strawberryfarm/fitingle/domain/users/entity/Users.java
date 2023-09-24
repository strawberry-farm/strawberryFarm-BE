package com.strawberryfarm.fitingle.domain.users.entity;

import com.strawberryfarm.fitingle.domain.AlertLog;
import com.strawberryfarm.fitingle.domain.Apply;
import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.Board;
import com.strawberryfarm.fitingle.domain.Comment;
import com.strawberryfarm.fitingle.domain.Groups;
import com.strawberryfarm.fitingle.domain.interestfield.entity.InterestField;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class  Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //apply 연관관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Apply> applies = new ArrayList<>();

    //alertLog 연관관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AlertLog> alertLogs = new ArrayList<>();

    //board 연관관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>();

    //Commnets 연관관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    // Groups 연관관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Groups> groups = new ArrayList<>();

    // interest_field 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<InterestField> interestFields = new ArrayList<>();

    // Qna 매핑
    @OneToMany(mappedBy = "user")
    private List<Qna> qnas = new ArrayList<>();

    //wish 매핑
    @OneToMany(mappedBy = "user")
    private List<Wish> wishes = new ArrayList<>();


    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String interestArea;

    @Column(nullable = false)
    private String profileImageUrl;

    private String loginToken;

    //firebase 용으로 넣음.
    private String pushToken;

    private String aboutMe;

    @Column(nullable = false)
    private String loginType;

    private LocalDateTime deleteDate;

    //연관관계 메서드
    public void addApply(Apply apply) {
        applies.add(apply);
        apply.setUser(this);
    }
    public void addBoard(Board board) {
        this.boards.add(board);
        board.setUser(this);  // Board에서 User로의 연관관계도 설정
    }
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setUser(this);
    }
    public void addGroup(Groups group) {
        this.groups.add(group);
        group.setUser(this);
    }
    public void addInterestField(InterestField interestField) {
        interestFields.add(interestField);
        interestField.setUser(this);
    }
    public void addQna(Qna qna) {
        qnas.add(qna);
        qna.setUser(this);
    }
    public void addWish(Wish wish) {
        wishes.add(wish);
        wish.setUser(this);
    }
}