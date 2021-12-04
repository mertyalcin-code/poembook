package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.PoemService;
import com.poembook.poembook.business.abstracts.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;


@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {
private PoemService poemService;
    @GetMapping("/test")
    void test() throws MessagingException {






    }
}
