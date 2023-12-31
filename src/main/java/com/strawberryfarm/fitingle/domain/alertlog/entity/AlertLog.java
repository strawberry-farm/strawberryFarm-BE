package com.strawberryfarm.fitingle.domain.alertlog.entity;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "alert_log")
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertType type;

    private String url;

    @Lob
    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String receiverEmail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertStatus status;

}
