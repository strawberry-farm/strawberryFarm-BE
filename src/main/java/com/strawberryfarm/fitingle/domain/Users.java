package com.strawberryfarm.fitingle.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
