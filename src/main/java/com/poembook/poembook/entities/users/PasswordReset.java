package com.poembook.poembook.entities.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long passwordResetId;
    private String email;
    @Column(unique = true)
    private String code;
    private ZonedDateTime creationDate;
}
