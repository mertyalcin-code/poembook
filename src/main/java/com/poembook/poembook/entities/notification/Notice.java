package com.poembook.poembook.entities.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poembook.poembook.entities.users.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;

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
