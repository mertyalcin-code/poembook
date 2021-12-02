package com.poembook.poembook.entities.users;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long passwordResetId;
    private String email;
    @Column(unique = true)
    private String code;
    private ZonedDateTime creationDate;
}
