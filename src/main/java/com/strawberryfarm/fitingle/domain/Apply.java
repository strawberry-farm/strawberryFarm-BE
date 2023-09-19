package com.strawberryfarm.fitingle.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "apply")
public class Apply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long boardId;

    @Lob
    private String contents;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

//    @Column(nullable = false)
//    private LocalDateTime created_date;
//
//    @Column(nullable = false)
//    private LocalDateTime updated_date;
}
