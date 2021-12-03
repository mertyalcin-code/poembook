package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.PoemLikeService;
import com.poembook.poembook.business.abstracts.PoemService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.utilities.result.Result;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like-poem")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class LikedPoemController {
    private static final String USER_DELETED_SUCCESSFULLY = "User deleted";
    PoemLikeService poemLikeService;
    UserService userService;
    PoemService poemService;

    @PostMapping("/like")
    public Result like(@RequestParam Long poemId
    ) {
        return poemLikeService.like(poemId);
    }

    @PostMapping("/unlike")
    public Result unlike(@RequestParam Long poemId) {
        return poemLikeService.unlike( poemId);
    }

    @PostMapping("/isliked")
    public Result isLiked(@RequestParam Long poemId) {
        return poemLikeService.isLiked( poemId);
    }

}
