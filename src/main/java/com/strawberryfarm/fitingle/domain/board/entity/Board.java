package com.strawberryfarm.fitingle.domain.board.entity;

import com.google.gson.Gson;
import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateRequestDTO;
import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import com.strawberryfarm.fitingle.domain.image.entity.Image;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.tag.entity.Tag;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;


    //분야 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fieldId")
    private Field field;

    // Groups 연관관계 매핑: 1:N 관계로 변경
    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Groups> groups = new ArrayList<>();

    //이미지 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    //Qna 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "board")
    private List<Qna> qnas = new ArrayList<>();

    //태그 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    //wish 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Wish> wishes = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Lob
    private String title;

    @Lob
    private String contents;

    @Column(nullable = false)
    private Long headCount;

//    @Column(nullable = false)
//    private String city;
//
//    @Column(nullable = false)
//    private String district;

    @Column(nullable = false)
    private String BCode;

    private String location;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String longitude;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Days days;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Times times;

//
//    @Column(nullable = false)
//    private Long views;

    private LocalDateTime deletedDate;

    public void setUser(Users users) {
        this.user = users;
    }


    // board 업데이트
    public void updateBoard(BoardUpdateRequestDTO dto) {
        this.title = dto.getTitle();
        this.contents = dto.getContents();
        this.postStatus = PostStatus.Y; // 또는 dto에서 상태를 받아서 설정
//        this.city = dto.getCity();
//        this.district = dto.getDistrict();
        List<String> questions = dto.getQuestion();
        Gson gson = new Gson();
        String jsonQuestions = gson.toJson(questions);
        this.headCount = dto.getHeadcount();
        this.BCode = dto.getBcode();
        this.location = dto.getDetail();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.question = jsonQuestions;
        this.days = Days.valueOf(dto.getDays());
        this.times = Times.valueOf(dto.getTimes());
    }


    //연관관계 메서드
    public void addField(Field field) {
        this.field = field;
        field.getBoards().add(this);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        if (tag.getBoard() != this) {
            tag.setBoard(this);
        }
    }

    public void addImage(Image image) {
        images.add(image);
        if (image.getBoard() != this) {
            image.setBoard(this);
        }
    }
    public void addQna(Qna qna){
        this.qnas.add(qna);
        if(qna.getBoard()!= this){
            qna.setBoard(this);
        }
    }
    public void addWish(Wish wish) {
        wishes.add(wish);
        if(wish.getBoard() != this) {
            wish.setBoard(this);
        }
    }

    // image 목록을 비우는 메소드
    public void clearImages() {
        this.images.clear();
    }

    // tag 목록을 비우는 메소드
    public void clearTags() {
        this.tags.clear();
    }

    public void addGroup(Groups group) {
        this.groups.add(group);
        group.setBoard(this);
    }

}
