package com.poembook.poembook.api.controller;

import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.exception.entities.UserNotFoundException;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.dtos.profile.ProfileUser;
import com.poembook.poembook.entities.users.User;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class UserController {
    private final UserService userService;
    @PostMapping("/self-update")

    public Result selfUpdate(@RequestParam String currentUsername,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String username,
                             @RequestParam String facebookAccount,
                             @RequestParam String twitterAccount,
                             @RequestParam String instagramAccount,
                             @RequestParam String aboutMe
    ) {
        return userService.selfUpdate(currentUsername, firstName, lastName, username, facebookAccount, twitterAccount, instagramAccount, aboutMe);
    }

    @GetMapping("/list/profile/{username}")
    public DataResult<ProfileUser> getUserProfile(@PathVariable String username) {
        return userService.getUserProfile(username);
    }


    @PostMapping("/change-password")
    public Result changePassword(@RequestParam String username, @RequestParam String password) {
        return userService.changePassword(username, password);
    }

    @PostMapping("/change-email")
    public Result changeEmail(@RequestParam String currentUsername,
                              @RequestParam String newEmail


    ) {
        return userService.changeEmail(currentUsername, newEmail);
    }

    @PostMapping("/change-username")
    public Result changeUsername(@RequestParam String currentUsername,
                                 @RequestParam String newUsername
    ) {
        return userService.changeUsername(currentUsername, newUsername);
    }

    @GetMapping("/list/username/{username}")
    public DataResult<User> findUserByUsername(@PathVariable("username") String username) throws UserNotFoundException {

        return userService.findUserByUsername(username);
    }
}
