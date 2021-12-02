package com.poembook.poembook.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long followersId;
    private ZonedDateTime followTime;
    @ManyToOne
    @JoinColumn(name = "from_user_fk")
    @JsonIgnore
    private User from;
    @ManyToOne
    @JoinColumn(name = "to_user_fk")
    @JsonIgnore
    private User to;


}