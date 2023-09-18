package com.strawberryfarm.fitingle.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@Table(name = "users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private String interestArea;
    private String profileImageUrl;
    private String loginToken;
    private String pushToken;
    private String aboutMe;
    private String loginType;

    private LocalDateTime deleteDate;

}
