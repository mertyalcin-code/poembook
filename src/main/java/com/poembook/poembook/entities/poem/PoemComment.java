package com.poembook.poembook.entities.poem;

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
public class PoemComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long poemCommentId;
    @Column(length = 5000)
    private String poemCommentText;
    private ZonedDateTime commentTime;
    private ZonedDateTime lastCommentUpdateTime;
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Poem poem;

}
