package com.poembook.poembook.entities.contact;

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
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formId;
    private ZonedDateTime formTime;
    private String formType;
    @Column(length = 5000)
    private String text;
    private String firstName;
    private String lastName;
    private String email;
}
