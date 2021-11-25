package com.poembook.poembook.api.controller;

import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.exception.entities.UserNotFoundException;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('superAdmin')")
public class SuperAdminController {
    private final UserService userService;

    @GetMapping("/make-super-admin/{username}")
    public Result makeSuperAdmin(@PathVariable("username") String username){

        return userService.makeSuperAdmin(username);
    }


    @DeleteMapping("/delete/{username}")
    public Result deleteUser(@PathVariable("username") String username){

        return userService.deleteUser(username);
    }
}
