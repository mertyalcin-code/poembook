package com.poembook.poembook.entities.poem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poembook.poembook.entities.category.Category;
import com.poembook.poembook.entities.users.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Poem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long poemId;
    private String poemTitle;
    @Column(length = 5000)
    private String poemContent;
    private boolean isActive;
    private ZonedDateTime creationDate;
    private ZonedDateTime lastUpdateDate;
    private int commentCount;
    private int howManyLikes;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @OneToMany(mappedBy = "poem")
    @JsonIgnore
    @ToString.Exclude
    private List<PoemLike> likedUsers;
    @ManyToOne
    @JoinColumn
    private Category category;
    @OneToMany(mappedBy = "poem")
    @JsonIgnore
    @ToString.Exclude
    private List<PoemComment> poemComments;

}
