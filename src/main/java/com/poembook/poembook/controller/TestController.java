package com.poembook.poembook.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;


@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    @GetMapping("/test")
    Date getPoems() {

        return Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }
}
