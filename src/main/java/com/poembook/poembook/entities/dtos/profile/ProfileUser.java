package com.poembook.poembook.entities.dtos.profile;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ProfileUser {
    List<ProfileFollowers> followers;
    List<ProfileFollowings> followings;
    private String firstName;
    private String lastName;
    private String username;
    private ZonedDateTime joinDate;
    private int poemCount;
    private String aboutMe;
    private String imageUrl;
    private String facebookAccount;
    private String twitterAccount;
    private String instagramAccount;
    private int followerCount;
    private int followingCount;


}
