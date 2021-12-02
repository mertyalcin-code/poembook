package com.poembook.poembook.entities.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poembook.poembook.entities.users.User;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long noticeId;
    private String noticeText;
    private ZonedDateTime noticeTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
