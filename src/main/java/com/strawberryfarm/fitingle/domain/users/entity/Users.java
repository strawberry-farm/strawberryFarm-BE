package com.strawberryfarm.fitingle.domain.users.entity;

import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.alertlog.entity.AlertLog;
import com.strawberryfarm.fitingle.domain.apply.entity.Apply;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.comment.entity.Comment;
import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import com.strawberryfarm.fitingle.domain.interestfield.entity.InterestField;
import com.strawberryfarm.fitingle.domain.keyword.entity.Keyword;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@Table(name = "users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
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

    //keyword 매핑
    @OneToMany(mappedBy = "users",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<Keyword> keywords = new ArrayList<>();

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles;

    @Column(nullable = false)
    private String nickname;

    private String BCode;

    @Column(nullable = false)
    private String profileImageUrl;

    private String loginToken;

    //firebase 용으로 넣음.
    private String pushToken;

    private String aboutMe;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SignUpType signUpType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UsersStatus status;

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
        if(comment.getUser()!= this){
            comment.setUser(this);
        }
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
        if (qna.getUser() != this) {
            qna.setUser(this);
        }
    }

    public void addWish(Wish wish) {
        wishes.add(wish);
        wish.setUser(this);
    }

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
        keyword.modifyUsers(this);
    }

    public void modifyNickname(String nickName) {
        this.nickname = nickName;

    }

    public void modifyUsersInfo(UsersDetailUpdateRequestDto usersDetailUpdateRequestDto) {
        this.profileImageUrl = usersDetailUpdateRequestDto.getProfileUrl();
        this.nickname = usersDetailUpdateRequestDto.getNickname();
        this.aboutMe = usersDetailUpdateRequestDto.getAboutMe();
    }

    public void modifyPassword(String password) {
        this.password = password;
    }

    public void modifyBCode(String interestArea) {
        this.BCode = interestArea;
    }
}
