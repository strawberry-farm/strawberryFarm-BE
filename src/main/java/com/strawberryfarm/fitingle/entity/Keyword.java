package com.strawberryfarm.fitingle.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "keyword")
public class Keyword {

    @Id
    @GeneratedValue
    private Long id;

}
