package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.utilities.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('superAdmin')")
public class SuperAdminController {
    private final UserService userService;

    @GetMapping("/make-super-admin/{username}")
    public Result makeSuperAdmin(@PathVariable String username) throws MessagingException {

        return userService.makeSuperAdmin(username);
    }


    @DeleteMapping("/delete/{username}")
    public Result deleteUser(@PathVariable String username) {

        return userService.deleteUser(username);
    }
}
