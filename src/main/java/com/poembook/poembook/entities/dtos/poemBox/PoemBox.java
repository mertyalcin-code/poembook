package com.poembook.poembook.entities.dtos.poemBox;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class PoemBox {
    List<PoemCommentBox> comments;
    private long poemId;
    private String poemTitle;
    private String poemContent;
    private boolean isActive;
    private ZonedDateTime creationDate;
    private long creationDateInMinute;
    private ZonedDateTime lastUpdateDate;
    private int commentCount;
    private int howManyLikes;
    private List<String> whoLiked;
    private String categoryTitle;
    private String username;
    private String firstName;
    private String lastName;
    private String avatar;
}
