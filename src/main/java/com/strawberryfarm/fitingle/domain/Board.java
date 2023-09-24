package com.strawberryfarm.fitingle.domain;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board")
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private Users user;

    //문의 연관관계 매핑
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    //분야 연관관계 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fieldId")
    private Field field;

    // Groups 연관관계 매핑
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Groups> groups = new ArrayList<>();

    //이미지 연관관계 매핑
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    //Qna 연관관계 매핑
    @OneToMany(mappedBy = "board")
    private List<Qna> qnas = new ArrayList<>();

    //태그 연관관계 매핑
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Image> tags = new ArrayList<>();

    //wish 연관관계 매핑
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Wish> wishes = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Lob
    private String titleContents;

    @Column(nullable = false)
    private Long headCount;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

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

    @Column(nullable = false)
    private Long views;

    private LocalDateTime deletedDate;

    public void setField(Field field) {
        this.field = field;
    }
    public void setUser(Users users) {
        this.user = users;
    }

    //연관관계 메서드
    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setBoard(this);
    }
    public void addGroup(Groups group) {
        this.groups.add(group);
        group.setBoard(this);
    }
    public void addImage(Image image) {
        images.add(image);
        image.setBoard(this);
    }


}
