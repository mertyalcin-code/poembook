package com.poembook.poembook.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer","handler","user"})
public class Avatar implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int avatarId;
    private String imageUrl;
    private ZonedDateTime uploadedDate;
    private String public_id;
    @OneToOne(mappedBy = "avatar")
    @JsonIgnore
    private User user;

    public Avatar(String imageUrl, ZonedDateTime uploadedDate, User user) {
        this.imageUrl = imageUrl;
        this.uploadedDate = uploadedDate;
        this.user = user;
    }

}