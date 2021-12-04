package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.FollowerService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/follower")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class FollowerController {
    private final FollowerService followerService;

    @PostMapping("/follow")
    public Result create(@RequestParam String fromUsername,
                         @RequestParam String toUsername) {
        return followerService.create(fromUsername, toUsername);
    }

    @PostMapping("/isfollowing")
    public Result isfollowing(@RequestParam String fromUsername,
                              @RequestParam String toUsername) {
        return followerService.isFollowing(fromUsername, toUsername);
    }

    @PostMapping("/unfollow")
    public Result delete(@RequestParam String fromUsername,
                         @RequestParam String toUsername

    ) {
        return followerService.delete(fromUsername, toUsername);
    }

    @PostMapping("/followers")
    public DataResult<List<String>> getFollowers(@RequestParam String username) {
        return followerService.getUsersFollowers(username);
    }

    @PostMapping("/following")
    public DataResult<List<String>> getFollowings(@RequestParam String username) {
        return followerService.getUsersFallowing(username);
    }

}
