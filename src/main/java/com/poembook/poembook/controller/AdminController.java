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
@PreAuthorize("hasAuthority('admin') or hasAuthority('superAdmin')")
public class AdminController {
    private final UserService userService;


    @PostMapping("/add")
    public Result addNewUser(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isActive") String isActive,
            @RequestParam("isNonLocked") String isNonLocked) throws MessagingException {
        return userService.addUser(currentUsername,firstName, lastName, username, email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive));
    }

    @PostMapping("/update")
    public Result updateUser(@RequestParam String adminUsername,
                             @RequestParam String userUsername,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String role,
                             @RequestParam String isActive,
                             @RequestParam String isNonLocked) {
        return userService.updateUser(adminUsername, userUsername, firstName, lastName, username, email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive));
    }

    @GetMapping("/list")
    public DataResult<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/list/id/{id}")
    public DataResult<User> findUserById(@PathVariable("id") Long id) {

        return userService.findUserById(id);
    }

    @GetMapping("/list/poets")
    public DataResult<List<User>> getAllPoets() {

        return userService.getAllPoets();
    }

    @GetMapping("/list/editors")
    public DataResult<List<User>> getAllEditors() {

        return userService.getAllEditors();
    }

    @GetMapping("/list/admins")
    public DataResult<List<User>> getAllAdmins() {

        return userService.getAllAdmins();
    }

    @GetMapping("/list/super-admins")
    public DataResult<List<User>> getAllSuperAdmins() {

        return userService.getAllSuperAdmins();
    }

    @GetMapping("/reset-password/{password}")
    public Result resetPassword(@PathVariable("password") String password) throws MessagingException {
        return userService.resetPassword(password);
    }



    @GetMapping("/list/username/{username}")
    public DataResult<User> findUserByUsername(@PathVariable("username") String username) throws UserNotFoundException {

        return userService.findUserByUsername(username);
    }


}
