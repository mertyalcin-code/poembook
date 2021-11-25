package com.poembook.poembook.entities.dtos.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileFollowings {
    private String username;
    private String firstName;
    private String lastName;
    private String imageUrl;
}
