package com.strawberryfarm.fitingle.domain.field.entity;

import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.domain.board.entity.Board;
import com.strawberryfarm.fitingle.domain.interestfield.entity.InterestField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;


@Entity
@Getter
@Table(name = "field")
public class Field extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "field", fetch = FetchType.LAZY)
    private Board board;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<InterestField> interestFields = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    //프론트 상의 필요, 디폴트 이미지 있다고 생각할지.
    @Column(nullable = false)
    private String imageUrl;

    //연관관계 메서드
    public void setBoard(Board board) {
        this.board = board;
    }
}
