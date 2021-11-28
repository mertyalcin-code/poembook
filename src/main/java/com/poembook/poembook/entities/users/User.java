package com.poembook.poembook.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.poembook.poembook.entities.notification.Notice;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemComment;
import com.poembook.poembook.entities.poem.PoemLike;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "person")
public class User implements Serializable {
    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private long userId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private Date lastLoginDate;
    private Date joinDate;
    private String role;
    @Column(length = 5000)
    private String authorities;
    private boolean isActive;
    private boolean isNotLocked;
    private int poemCounts;
    private String facebookAccount;
    private String twitterAccount;
    private String instagramAccount;
    private String aboutMe;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Poem> poems;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @ToString.Exclude
    private List<PoemLike> likedPoems;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @ToString.Exclude
    private List<PoemComment> PoemComments;
    @OneToMany(mappedBy = "to")
    @JsonIgnore
    @ToString.Exclude
    private List<Follower> followers;
    @OneToMany(mappedBy = "from")
    @JsonIgnore
    @ToString.Exclude
    private List<Follower> following;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @ToString.Exclude
    private List<Notice> notices;
    @OneToOne(cascade = CascadeType.ALL)
    private Avatar avatar;

    public String[] getAuthorities() {
        String[] strings = authorities.replace("[", "").replace("]", "").split(", ");
        String[] result = new String[strings.length];
        System.arraycopy(strings, 0, result, 0, result.length);
        return result;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = Arrays.toString(authorities);
    }


}
