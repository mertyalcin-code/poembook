package com.poembook.poembook.entities.dtos.poemBox;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PoemBox {
    List<PoemCommentBox> comments;
    private long poemId;
    private String poemTitle;
    private String poemContent;
    private boolean isActive;
    private Date creationDate;
    private long creationDateInMinute;
    private Date lastUpdateDate;
    private int commentCount;
    private int howManyLikes;
    private List<String> whoLiked;
    private String categoryTitle;
    private String username;
    private String firstName;
    private String lastName;
    private String avatar;
}
