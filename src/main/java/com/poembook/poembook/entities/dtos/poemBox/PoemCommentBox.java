package com.poembook.poembook.entities.dtos.poemBox;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class PoemCommentBox {
    private long poemCommentId;
    private String poemCommentText;
    private long commentTimeInMinute;
    private ZonedDateTime lastCommentUpdateTime;
    private String username;
    private String userAvatar;
    private String userFirstName;
    private String userLastName;
}
