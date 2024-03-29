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
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "field")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Field extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "field", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>(); // 여기를 수정했습니다.

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<InterestField> interestFields = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    // 연관관계 편의 메서드
    public void setBoard(Board board) {
        this.boards.add(board);
        if (board.getField() != this) {
            board.addField(this);
        }
    }
}
