package com.poembook.poembook.entities.poem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poembook.poembook.entities.users.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class PoemLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long likedPoemId;
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    User user;
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    Poem poem;

    Date likedAt;

}
