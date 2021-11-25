package com.poembook.poembook.entities.dtos.profile;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProfileUser {
    List<ProfileFollowers> followers;
    List<ProfileFollowings> followings;
    private String firstName;
    private String lastName;
    private String username;
    private Date joinDate;
    private int poemCount;
    private String aboutMe;
    private String imageUrl;
    private String facebookAccount;
    private String twitterAccount;
    private String instagramAccount;
    private int followerCount;
    private int followingCount;


}
