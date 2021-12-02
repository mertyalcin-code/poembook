package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.PrivateMessageService;
import com.poembook.poembook.core.utilities.result.Result;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private-message")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class PrivateMessageController {
    private PrivateMessageService privateMessageService;

    @PostMapping("/send")
    public Result send(@RequestParam String fromUsername,
                       @RequestParam String toUsername,
                       @RequestParam String message

    ) {
        return privateMessageService.sendMessage(fromUsername, toUsername, message);
    }

    @PostMapping("/list/{username}")
    public Result usersAllMessages(@PathVariable String username

    ) {
        return privateMessageService.usersAllMessages(username);
    }

    @PostMapping("/list-all-messages")
    public Result usersAllMessagesWith(@RequestParam String username,
                                       @RequestParam String withUsername

    ) {
        return privateMessageService.usersAllMessagesWith(username, withUsername);
    }

    @GetMapping("/list-message-list/{username}")
    public Result usersMessageList(@PathVariable String username
    ) {
        return privateMessageService.usersMessageList(username);
    }
}
